#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Phase 1 - Generates the pre-populated SQLite asset database that Room loads
through `createFromAsset("ma_fantatra.db")`.

Usage:
    python tools/build_db.py

The schema (tables, columns, indices) is taken VERBATIM from the Room schema
JSON exported by ksp (app/schemas/<database>/<version>.json), so the asset is
structurally identical to what Room expects. Only the seed rows below are
maintained by hand.
"""

import json
import os
import sqlite3
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCHEMAS_DIR = os.path.join(ROOT, "app", "schemas")
ASSET_PATH = os.path.join(ROOT, "app", "src", "main", "assets", "databases", "ma_fantatra.db")

# Keyed by exact Room table names (column names must match the exported schema).
PROCEDURES = [
    {
        "id": 1,
        "title": "Carte Nationale d'Identité (CNI)",
        "category": "Cartes & Identité",
        "cost": "2 000 Ariary",
        "processingTime": "10 à 15 jours ouvrables",
        "description": "Demande ou renouvellement de la Carte Nationale d'Identité. Pour les citoyens de 14 ans et plus, elle est délivrée par la Commune après visa du Fokontany de résidence.",
        "instructions": "1. Se présenter au bureau du Fokontany de son lieu de résidence.\n2. Retirer et remplir le formulaire de demande de CNI.\n3. Faire viser le formulaire par le chef de Fokontany.\n4. Déposer le dossier complet au service de l'état civil de la Commune.\n5. S'acquitter des frais (2 000 Ariary) à la recette communale et conserver le récépissé.\n6. Revenir retirer la carte à la date indiquée sur le récépissé.",
    },
    {
        "id": 2,
        "title": "Certificat de résidence",
        "category": "Attestations",
        "cost": "Gratuit",
        "processingTime": "24 heures à 3 jours ouvrables",
        "description": "Attestation justifiant l'adresse et la résidence d'une personne dépendant d'un ménage du Fokontany. Délivrée par le chef de Fokontany.",
        "instructions": "1. Se présenter au bureau du Fokontany.\n2. Présenter sa pièce d'identité.\n3. Remplir la demande de certificat de résidence.\n4. Faire viser par le responsable du recensement du quartier (secrétaire de Fokontany).\n5. Retirer le certificat (gratuit).",
    },
    {
        "id": 3,
        "title": "Extrait d'acte de naissance",
        "category": "État Civil",
        "cost": "Gratuit (dans les délais) ou 2 000 Ariary (copie)",
        "processingTime": "3 à 7 jours ouvrables",
        "description": "Extrait ou copie intégrale de l'acte de naissance, délivré par le service de l'état civil de la Commune de naissance.",
        "instructions": "1. Se rendre au service de l'état civil de la Commune où l'acte a été établi.\n2. Remplir le formulaire de demande d'extrait ou de copie intégrale.\n3. Présenter les pièces justificatives.\n4. Payer les frais éventuels et retirer le document à la date indiquée.",
    },
    {
        "id": 4,
        "title": "Acte de naissance par jugement supplétif",
        "category": "État Civil",
        "cost": "8 000 Ariary (frais et timbres)",
        "processingTime": "1 à 3 mois",
        "description": "Pour toute personne dont l'acte de naissance n'a jamais été dressé (déclaration tardive hors délai légal). Procédure judiciaire engagée à travers la Commune.",
        "instructions": "1. Obtenir un certificat de non-inscription auprès de la Commune de naissance.\n2. Déposer une requête au tribunal via le service de l'état civil.\n3. Remplir les formulaires avec l'appui du service de l'état civil.\n4. Après le jugement, faire transcrire l'acte au registre de l'état civil.\n5. Demander ensuite l'extrait définitif.",
    },
    {
        "id": 5,
        "title": "Acte de mariage",
        "category": "État Civil",
        "cost": "20 000 Ariary",
        "processingTime": "1 mois (après la publication des bans)",
        "description": "Célébration du mariage et délivrance de l'acte de mariage par l'officier de l'état civil de la Commune.",
        "instructions": "1. Déposer les dossiers des deux futurs époux à la Commune.\n2. Procéder à la publication des bans pendant la période réglementaire.\n3. Célébrer le mariage devant l'officier de l'état civil.\n4. Retirer l'acte de mariage et le livret de famille.",
    },
    {
        "id": 6,
        "title": "Acte de décès",
        "category": "État Civil",
        "cost": "Gratuit",
        "processingTime": "Sur place (le jour même)",
        "description": "Déclaration et transcription de l'acte de décès au service de l'état civil de la Commune du lieu de décès.",
        "instructions": "1. Obtenir le certificat de décès auprès d'un médecin.\n2. Déclarer le décès au Fokontany du lieu de résidence.\n3. Se présenter au service de l'état civil de la Commune.\n4. Retirer l'acte de décès.",
    },
    {
        "id": 7,
        "title": "Attestation de prise en charge",
        "category": "Attestations",
        "cost": "2 000 Ariary",
        "processingTime": "24 heures",
        "description": "Attestation établissant qu'une personne prend en charge une autre (scolarisation, hospitalisation, voyage) auprès d'un établissement ou d'une administration.",
        "instructions": "1. Se présenter au bureau du Fokontany.\n2. Remplir la demande d'attestation de prise en charge.\n3. Présenter les pièces du souscripteur et de la personne prise en charge.\n4. Faire viser par le chef de Fokontany et retirer l'attestation.",
    },
    {
        "id": 8,
        "title": "Attestation de non-inscription",
        "category": "Divers",
        "cost": "2 000 Ariary",
        "processingTime": "24 heures à 3 jours ouvrables",
        "description": "Certificat attestant qu'aucun acte de naissance ou de mariage n'a été enregistré pour une personne sur les registres de la Commune. Souvent requis avant un jugement supplétif.",
        "instructions": "1. Se présenter au service de l'état civil de la Commune de naissance.\n2. Remplir la demande de certificat de non-inscription.\n3. Justifier de son identité.\n4. Retirer l'attestation après vérification des registres.",
    },
]

DOCUMENT_REQUIREMENTS = [
    # Procedure 1 - CNI
    {"id": 1, "procedureId": 1, "title": "Formulaire de demande de CNI dûment rempli", "isMandatory": True, "note": "Retiré au Fokontany ou à la Commune."},
    {"id": 2, "procedureId": 1, "title": "Certificat de résidence délivré par le Fokontany", "isMandatory": True, "note": None},
    {"id": 3, "procedureId": 1, "title": "Copie intégrale de l'acte de naissance ou livret de famille", "isMandatory": True, "note": None},
    {"id": 4, "procedureId": 1, "title": "Deux photos d'identité récentes fond clair", "isMandatory": True, "note": None},
    {"id": 5, "procedureId": 1, "title": "Autorisation parentale si âge de 14 à 18 ans", "isMandatory": True, "note": "Signée devant l'officier de l'état civil."},
    {"id": 6, "procedureId": 1, "title": "Avis d'insertion au journal officiel", "isMandatory": False, "note": "En cas de perte ou de vol de la carte."},
    # Procedure 2 - Certificat de résidence
    {"id": 7, "procedureId": 2, "title": "Récépissé de recensement ou de domiciliation au Fokontany", "isMandatory": True, "note": None},
    {"id": 8, "procedureId": 2, "title": "Carte d'identité ou carte d'élève / étudiant", "isMandatory": True, "note": None},
    {"id": 9, "procedureId": 2, "title": "Pièce d'identité du chef de ménage", "isMandatory": True, "note": None},
    {"id": 10, "procedureId": 2, "title": "Facture d'eau ou d'électricité", "isMandatory": False, "note": "Justificatif de domicile selon la commune."},
    # Procedure 3 - Extrait acte de naissance
    {"id": 11, "procedureId": 3, "title": "Pièce d'identité du demandeur", "isMandatory": True, "note": "CNI, passeport ou carte d'élève."},
    {"id": 12, "procedureId": 3, "title": "Livret de famille ou numéro de l'acte et registre", "isMandatory": True, "note": None},
    {"id": 13, "procedureId": 3, "title": "CNI des deux parents", "isMandatory": True, "note": "Si la demande concerne un enfant mineur."},
    {"id": 14, "procedureId": 3, "title": "Procuration légalisée", "isMandatory": False, "note": "Si la demande est faite par un tiers."},
    # Procedure 4 - Jugement supplétif
    {"id": 15, "procedureId": 4, "title": "Certificat de non-inscription sur les registres", "isMandatory": True, "note": None},
    {"id": 16, "procedureId": 4, "title": "Actes de naissance des deux parents", "isMandatory": True, "note": None},
    {"id": 17, "procedureId": 4, "title": "Carte d'identité du demandeur", "isMandatory": True, "note": None},
    {"id": 18, "procedureId": 4, "title": "Pièces justificatives de la date et du lieu de naissance", "isMandatory": True, "note": "Livret scolaire, certificats, témoignages."},
    {"id": 19, "procedureId": 4, "title": "Lettre de demande du maire de la Commune", "isMandatory": True, "note": None},
    # Procedure 5 - Mariage
    {"id": 20, "procedureId": 5, "title": "CNI des deux futurs époux", "isMandatory": True, "note": None},
    {"id": 21, "procedureId": 5, "title": "Extraits d'acte de naissance de moins de 3 mois", "isMandatory": True, "note": None},
    {"id": 22, "procedureId": 5, "title": "Certificat de résidence de chacun", "isMandatory": True, "note": None},
    {"id": 23, "procedureId": 5, "title": "Deux témoins majeurs munis de leur CNI", "isMandatory": True, "note": None},
    {"id": 24, "procedureId": 5, "title": "Autorisation parentale", "isMandatory": False, "note": "Si l'un des époux a moins de 21 ans."},
    # Procedure 6 - Décès
    {"id": 25, "procedureId": 6, "title": "Certificat de décès établi par un médecin", "isMandatory": True, "note": None},
    {"id": 26, "procedureId": 6, "title": "Carte d'identité du déclarant", "isMandatory": True, "note": None},
    {"id": 27, "procedureId": 6, "title": "Carte d'identité du défunt (si disponible)", "isMandatory": True, "note": None},
    {"id": 28, "procedureId": 6, "title": "Justificatif du lieu de décès", "isMandatory": True, "note": "Attestation du chef de ménage ou du Fokontany."},
    # Procedure 7 - Prise en charge
    {"id": 29, "procedureId": 7, "title": "Carte d'identité du souscripteur", "isMandatory": True, "note": None},
    {"id": 30, "procedureId": 7, "title": "Carte d'identité ou acte de naissance de la personne prise en charge", "isMandatory": True, "note": None},
    {"id": 31, "procedureId": 7, "title": "Certificat de résidence du souscripteur", "isMandatory": True, "note": None},
    # Procedure 8 - Non-inscription
    {"id": 32, "procedureId": 8, "title": "Carte d'identité du demandeur", "isMandatory": True, "note": None},
    {"id": 33, "procedureId": 8, "title": "Actes de naissance des parents", "isMandatory": True, "note": None},
    {"id": 34, "procedureId": 8, "title": "Livret de famille", "isMandatory": False, "note": None},
]

COMMUNES = [
    # District Antananarivo-Renivohitra
    {"id": 1, "name": "Antananarivo-Renivohitra", "districtName": "Antananarivo-Renivohitra", "regionName": "Analamanga", "latitude": -18.9076, "longitude": 47.5367},
    # District Antananarivo-Atsimondrano (26 communes)
    {"id": 2, "name": "Alakamisy Fenoarivo", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9839, "longitude": 47.4967},
    {"id": 3, "name": "Alatsinainy Ambazaha", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9389, "longitude": 47.5564},
    {"id": 4, "name": "Ambalavao", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9303, "longitude": 47.4931},
    {"id": 5, "name": "Ambatofahavalo", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9333, "longitude": 47.4552},
    {"id": 6, "name": "Ambavahaditokana", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9772, "longitude": 47.5325},
    {"id": 7, "name": "Ambohidrapeto", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9219, "longitude": 47.4189},
    {"id": 8, "name": "Ambohijanaka", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9339, "longitude": 47.5339},
    {"id": 9, "name": "Ampahitrosy", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 10, "name": "Ampanefy", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 11, "name": "Ampitatafika", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9386, "longitude": 47.5417},
    {"id": 12, "name": "Andoharanofotsy", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9547, "longitude": 47.5261},
    {"id": 13, "name": "Andranonahoatra", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9381, "longitude": 47.4425},
    {"id": 14, "name": "Androhibe Antsahadinta", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9839, "longitude": 47.4628},
    {"id": 15, "name": "Ankadimanga", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.8961, "longitude": 47.5628},
    {"id": 16, "name": "Ankaraobato", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9231, "longitude": 47.4664},
    {"id": 17, "name": "Anosizato Andrefana", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9047, "longitude": 47.5022},
    {"id": 18, "name": "Antanetikely", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 19, "name": "Bemasoandro", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9736, "longitude": 47.5336},
    {"id": 20, "name": "Bongatsara", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 21, "name": "Fenoarivo", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9589, "longitude": 47.5389},
    {"id": 22, "name": "Fiombonana", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9339, "longitude": 47.5517},
    {"id": 23, "name": "Itaosy", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9589, "longitude": 47.5047},
    {"id": 24, "name": "Soalandy", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 25, "name": "Soavina", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.8558, "longitude": 47.5233},
    {"id": 26, "name": "Tanjombato", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9242, "longitude": 47.4942},
    {"id": 27, "name": "Tsiafahy", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.8797, "longitude": 47.4931},
    # District Antananarivo-Avaradrano (16 communes)
    {"id": 28, "name": "Alasora", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.9306, "longitude": 47.5489},
    {"id": 29, "name": "Ambohidrabiby", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.8722, "longitude": 47.5125},
    {"id": 30, "name": "Ambohimalaza Miray", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.8897, "longitude": 47.6244},
    {"id": 31, "name": "Ambohimanambola", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 32, "name": "Ambohimanga Rova", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.7608, "longitude": 47.5628},
    {"id": 33, "name": "Ambohimangakely", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.8847, "longitude": 47.5847},
    {"id": 34, "name": "Anjeva Gara", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 35, "name": "Ankadikely Ilafy", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.8536, "longitude": 47.5614},
    {"id": 36, "name": "Ankadinandriana", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 37, "name": "Anosy Avaratra", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 38, "name": "Fieferana", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 39, "name": "Manandriana", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.8956, "longitude": 47.5325},
    {"id": 40, "name": "Masindray", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 41, "name": "Sabotsy Namehana", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.8369, "longitude": 47.5403},
    {"id": 42, "name": "Talata Volonondry", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    {"id": 43, "name": "Viliahazo", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": None, "longitude": None},
    # District Antsirabe I (Vakinankaratra)
    {"id": 44, "name": "Antsirabe I", "districtName": "Antsirabe I", "regionName": "Vakinankaratra", "latitude": -19.8659, "longitude": 47.0333},
]

FOKONTANY = [
    {"id": 1, "name": "Ambohijatovo", "communeName": "Antananarivo-Renivohitra", "districtName": "Antananarivo-Renivohitra", "regionName": "Analamanga", "latitude": -18.9068, "longitude": 47.5272, "addressNote": "Près du marché d'Ambohijatovo, 1er arrondissement.", "openingHours": "Lun–Ven : 8h00 – 12h00 / 14h00 – 17h00"},
    {"id": 2, "name": "Analakely", "communeName": "Antananarivo-Renivohitra", "districtName": "Antananarivo-Renivohitra", "regionName": "Analamanga", "latitude": -18.9044, "longitude": 47.5186, "addressNote": "Centre-ville, près de la gare Soarano.", "openingHours": "Lun–Sam : 8h00 – 12h00 / 14h00 – 16h30"},
    {"id": 3, "name": "67 Ha", "communeName": "Antananarivo-Renivohitra", "districtName": "Antananarivo-Renivohitra", "regionName": "Analamanga", "latitude": -18.9106, "longitude": 47.5428, "addressNote": "Quartier des 67 hectares, près du Stade municipal.", "openingHours": "Lun–Ven : 8h00 – 17h00"},
    {"id": 4, "name": "Ampefiloha", "communeName": "Antananarivo-Renivohitra", "districtName": "Antananarivo-Renivohitra", "regionName": "Analamanga", "latitude": -18.8934, "longitude": 47.5099, "addressNote": "Faisant face à la gare Ampefiloha.", "openingHours": "Lun–Ven : 8h00 – 12h00 / 14h00 – 17h00"},
    {"id": 5, "name": "Ankadifilao", "communeName": "Antananarivo-Atsimondrano", "districtName": "Antananarivo-Atsimondrano", "regionName": "Analamanga", "latitude": -18.9183, "longitude": 47.5118, "addressNote": "Avenue de l'OUVERTURE, à 500 m de la route nationale 7.", "openingHours": "Lun–Ven : 8h00 – 12h00 / 14h00 – 17h00"},
    {"id": 6, "name": "Androhibe", "communeName": "Antananarivo-Avaradrano", "districtName": "Antananarivo-Avaradrano", "regionName": "Analamanga", "latitude": -18.8594, "longitude": 47.4913, "addressNote": "Chef-lieu de la commune rurale d'Antananarivo-Avaradrano.", "openingHours": "Lun–Ven : 8h00 – 12h00 / 14h00 – 16h00"},
    {"id": 7, "name": "Alarobia Amboniloha", "communeName": "Antsirabe I", "districtName": "Antsirabe I", "regionName": "Vakinankaratra", "latitude": -19.8700, "longitude": 47.0333, "addressNote": "Arrondissement sud de la commune urbaine d'Antsirabe.", "openingHours": "Lun–Ven : 8h00 – 12h00 / 14h00 – 17h00"},
    {"id": 8, "name": "Ambohimanarina", "communeName": "Antsirabe I", "districtName": "Antsirabe I", "regionName": "Vakinankaratra", "latitude": -19.8550, "longitude": 47.0280, "addressNote": "Près de la gare d'Antsirabe.", "openingHours": "Lun–Ven : 8h00 – 12h00 / 14h00 – 17h00"},
]

SEED_DATA = {
    "procedure": PROCEDURES,
    "document_requirement": DOCUMENT_REQUIREMENTS,
    "fokontany": FOKONTANY,
    "commune": COMMUNES,
}


def find_schema_file():
    if not os.path.isdir(SCHEMAS_DIR):
        sys.exit("Schema export directory not found: %s\nRun `gradlew :app:kspDebugKotlin` first." % SCHEMAS_DIR)

    def version_key(name):
        try:
            return int(os.path.splitext(name)[0])
        except ValueError:
            return 0

    candidates = []
    for root, _dirs, files in os.walk(SCHEMAS_DIR):
        candidates.extend((version_key(name), os.path.join(root, name)) for name in files if name.endswith(".json"))
    if not candidates:
        sys.exit("No Room schema JSON found under %s. Run `gradlew :app:kspDebugKotlin` first." % SCHEMAS_DIR)

    # Return the highest exported schema version (the current one).
    return max(candidates, key=lambda item: item[0])[1]


def quote_ident(identifier):
    return "`" + identifier.replace("`", "``") + "`"


def insert_rows(conn, table_name, column_names, rows):
    if not rows:
        return
    columns = ", ".join(quote_ident(c) for c in column_names)
    placeholders = ", ".join("?" for _ in column_names)
    sql = "INSERT INTO %s (%s) VALUES (%s)" % (quote_ident(table_name), columns, placeholders)
    conn.executemany(
        sql,
        [
            [row.get(column) for column in column_names]
            for row in rows
        ],
    )


def main():
    schema_path = find_schema_file()
    with open(schema_path, encoding="utf-8") as handle:
        schema = json.load(handle)

    database = schema["database"]
    version = database["version"]
    identity_hash = database["identityHash"]
    entities = {e["tableName"]: e for e in database["entities"]}

    os.makedirs(os.path.dirname(ASSET_PATH), exist_ok=True)
    if os.path.exists(ASSET_PATH):
        os.remove(ASSET_PATH)

    conn = sqlite3.connect(ASSET_PATH)
    try:
        cursor = conn.cursor()
        for entity in database["entities"]:
            # Room substitutes ${TABLE_NAME} at runtime; we do it here.
            cursor.execute(entity["createSql"].replace("${TABLE_NAME}", entity["tableName"]))
        for setup_query in database.get("setupQueries", []):
            cursor.execute(setup_query)
        conn.execute("PRAGMA user_version = %d" % version)
        cursor.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, ?)", (identity_hash,))

        for table_name, rows in SEED_DATA.items():
            entity = entities.get(table_name)
            if entity is None:
                raise LookupError("Schema export has no entity table named %r" % table_name)
            column_names = [field["columnName"] for field in entity["fields"]]
            insert_rows(conn, table_name, column_names, rows)

        conn.commit()

        cursor.execute("SELECT COUNT(*) FROM `procedure`")
        procedure_count = cursor.fetchone()[0]
        cursor.execute("SELECT COUNT(*) FROM document_requirement")
        document_count = cursor.fetchone()[0]
        cursor.execute("SELECT COUNT(*) FROM fokontany")
        fokontany_count = cursor.fetchone()[0]
        cursor.execute("SELECT COUNT(*) FROM commune")
        commune_count = cursor.fetchone()[0]
    finally:
        conn.close()

    print("Wrote %s" % ASSET_PATH)
    print("  schema version : %s (from %s)" % (version, schema_path))
    print("  procedures     : %d" % procedure_count)
    print("  requirements   : %d" % document_count)
    print("  fokontany      : %d" % fokontany_count)
    print("  communes       : %d" % commune_count)


if __name__ == "__main__":
    main()