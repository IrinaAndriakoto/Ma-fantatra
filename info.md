# Ma-fantatra — Architecture technique complète

## 1. Le code source — où tout commence

Les entités Room (table SQLite) sont définies en Kotlin avec des annotations :

```
app/src/main/java/com/ma_fantatra/data/local/entity/
├── ProcedureEntity.kt      → @Entity(tableName = "procedure")
├── FokontanyEntity.kt      → @Entity(tableName = "fokontany")
└── CommuneEntity.kt        → @Entity(tableName = "commune")
```

La DB est déclarée dans `MafantatraDatabase.kt` avec sa **version** (2) et ses migrations :

```kotlin
@Database(
    entities = [ProcedureEntity::class, DocumentRequirementEntity::class, FokontanyEntity::class, CommuneEntity::class],
    version = 2
)
abstract class MafantatraDatabase : RoomDatabase() {
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `commune` (...)")
            }
        }
    }
}
```

---

## 2. KSP — le générateur automatique

KSP (Kotlin Symbol Processing) est le processeur d'annotations de Room. Il tourne **pendant le build Gradle** :

```powershell
.\gradlew :app:kspDebugKotlin
```

Ce que KSP fait :
1. Lit toutes les classes `@Entity`, `@Dao`, `@Database`
2. **Génère** les implémentations Java des DAOs (code que vous ne voyez pas)
3. **Exporte le schéma** de la DB dans un fichier JSON

Le résultat :
```
app/schemas/com.ma_fantatra.data.local.MafantatraDatabase/
├── 1.json    ← schéma quand la DB était en version 1
└── 2.json    ← schéma après ajout de CommuneEntity (version 2)
```

**Chaque fichier JSON contient :**
- La structure exacte de chaque table (colonnes, types, clés primaires)
- Le `identityHash` (empreinte unique du schéma)
- La version de la DB

**`2.json` est généré automatiquement** quand vous changez le schéma Room et lancez le build. Vous ne l'éditez jamais à la main.

---

## 3. build_db.py — la seed Android embarquée

Ce script Python lit le JSON le plus récent et génère le fichier SQLite embarqué dans l'APK :

```
tools/build_db.py
  │
  ├── Lit app/schemas/.../2.json (schéma v2, 4 tables)
  ├── Crée ma_fantatra.db avec les tables du schéma
  ├── Insère les données seed (8 procédures, 34 docs, 23 fokontany, 44 communes)
  └── Écrit le tout dans app/src/main/assets/databases/ma_fantatra.db
```

**Résultat :** un fichier `ma_fantatra.db` qui est une copie fidèle de la Room DB v2, pré-remplie. Ce fichier est **copié dans l'APK** et distribué aux utilisateurs.

---

## 4. L'APK — le paquet distribué

Quand Gradle assemble l'APK :

```
app/build/outputs/apk/debug/app-debug.apk
  │
  ├── Code compilé (classes Kotlin → bytecode)
  ├── Ressources (icônes, strings.xml, thèmes)
  └── assets/databases/ma_fantatra.db  ← la seed DB copiée ici
```

L'APK est installé sur l'émulateur ou le téléphone.

---

## 5. Premier lancement — Room copie l'asset

Au démarrage de l'app, `DatabaseModule.kt` exécute :

```kotlin
Room.databaseBuilder(context, MafantatraDatabase::class.java, "ma_fantatra.db")
    .createFromAsset("ma_fantatra.db")
    .addMigrations(MafantatraDatabase.MIGRATION_1_2)
    .build()
```

**Ce qui se passe :**
1. Room vérifie si une DB existe déjà dans le stockage privé de l'app
2. **Si non (premier lancement)** : copie `ma_fantatra.db` depuis les assets vers le stockage privé
3. **Si oui** : vérifie la version de la DB copiée vs. la version attendue (2)
   - Si version asset < version attendue → exécute les migrations (`MIGRATION_1_2` ajoute la table `commune`)
   - Si version asset = version attendue → rien à faire
4. Vérifie le `identityHash` pour s'assurer que le schéma correspond

---

## 6. Le backend FastAPI — la source de vérité live

Le backend est un **autre projet** séparé :

```
backend/
├── app/
│   ├── main.py          → FastAPI app, routes /api/procedures, /api/communes, etc.
│   ├── models.py        → ORM SQLAlchemy (Procedure, Commune, Fokontany...)
│   ├── database.py      → engine SQLite (backend/ma_fantatra.db)
│   └── seed_data.py     → Remplit la DB backend avec les mêmes données
└── ma_fantatra.db       → La DB du serveur (générée par seed_data.py)
```

**seed_data.py** remplit cette DB serveur. Ce n'est pas la même DB que celle de l'APK — c'est la DB que l'API sert via HTTP.

**uvicorn** lance le serveur : `http://127.0.0.1:8000/api/procedures`, `/api/communes`, etc.

---

## 7. L'app Android en runtime — le flux offline-first

Quand l'utilisateur ouvre l'app :

```
CommuneViewModel.init()
    │
    ├── 1. Room.emit(communeDao.observeAll()) → Flow<List<Commune>>
    │      → UI affiche les données locales immédiatement (asset seed ou sync précédente)
    │
    └── 2. viewModelScope.launch { repository.refresh() }
             │
             ├── Succès : api.getCommunes() → 44 communes → communeDao.upsertAll(...)
             │            → Flow émet les nouvelles données → UI se met à jour
             │
             └── Échec (offline) : try/catch silencieux → le Flow continue à servir le cache
```

**Le même mécanisme tourne en arrière-plan** toutes les 6h via `SyncWorker` (WorkManager), même si l'app n'est pas ouverte.

---

## 8. Résumé du cycle complet

```
ÉDITER UN ENTITY KOTLIN
        │
        ▼
.\gradlew :app:kspDebugKotlin
        │
        ├── Génère les DAOs (code Java)
        └── Exporte le schéma dans app/schemas/.../2.json
                │
                ▼
        python tools/build_db.py
                │
                └── Crée assets/databases/ma_fantatra.db
                        (tables du schéma + seed data)
                                │
                                ▼
                .\gradlew :app:assembleDebug
                                │
                                └── APK avec la seed DB embarquée
                                        │
                                        ▼
                                Installation sur l'appareil
                                        │
                                        ▼
                        Room copie l'asset (ou migre)
                        → DB locale avec seed data
                                │
                                ▼
                        App ouverte → ViewModel.refresh()
                        → Sync depuis l'API si réseau dispo
                        → Sinon, le cache suffit
```

---

## 9. Les couches de l'application Android

### data/local/entity/* — les tables SQL (≈ @Entity JPA)
Classes Kotlin représentant une ligne de table. Ce sont les **mêmes** que les `@Entity` dans un projet Spring Boot.
Ex: `ProcedureEntity.kt` = `@Entity` avec des champs = colonnes de la table `procedure`.

### domain/model/* — les objets métier (≈ DTO/POJO Spring)
Mêmes données mais **sans dépendance Android**. Utilisés par les couches supérieures (UI).
Ex: `data class Commune(id, name, districtName...)` — pas d'annotation Room, pas d'import Android.
En Spring : c'est comme un POJO qu'on passerait entre le controller et le service.

### domain/repository/* — les interfaces métier (≈ Repository Spring)
Définissent **ce qu'on peut faire** avec les données, sans dire **comment**.
Ex: `interface CommuneRepository { fun observeAll(): Flow<List<Commune>>; suspend fun refresh() }`
En Spring : comme une interface `@Repository` ou un service abstrait. La UI ne connaît que l'interface.

### data/repository/* — les implémentations (≈ @Repository Spring concrète)
Les classes qui **exécutent** le code : lecture Room + appel API.
Ex: `RoomCommuneRepository(communeDao, api)` — lit le cache Room et tente de rafraîchir depuis l'API.
En Spring : c'est le bean `@Repository` qui contient la logique d'accès aux données.

### ui/* — les écrans (≈ Composants Angular / Controllers Spring)
Chaque dossier `ui/feature/` contient :
- **Screen.kt** = le composant UI (≈ un composant Angular avec template HTML)
- **ViewModel.kt** = la logique (≈ un service Angular ou un controller Spring qui prépare les données pour la vue)

Ex: `DirectoryScreen.kt` affiche la liste ; `DirectoryViewModel.kt` gère les données et les appels réseau.

### di/* — l'injection de dépendances (≈ @Inject / @Bean Spring)
Les modules Hilt qui disent à l'app : "quand quelqu'un demande un `CommuneRepository`, crée un `RoomCommuneRepository`".
En Spring : comme les `@Configuration` classes avec des `@Bean` methods.
En Angular : comme les providers dans `providers: [...]`.

---

## 10. Comparaison avec Spring Boot / Angular

| Concept Android | Spring Boot | Angular |
|---|---|---|
| `@Entity` (Room) | `@Entity` (JPA) | — |
| `@Dao` | `@Repository` | — |
| `MafantatraDatabase` | `application.properties` (DB config) | — |
| `domain/model/` | POJOs / DTOs | Models (interfaces TypeScript) |
| `domain/repository/` | Service interfaces | Services (abstract) |
| `data/repository/` | `@Repository` impl | Service impl (avec HttpClient) |
| `ui/Screen.kt` | Thymeleaf template / REST view | Component (.html + .ts) |
| `ui/ViewModel.kt` | `@Controller` / `@Service` | Component .ts (logique) |
| `di/Module.kt` | `@Configuration` + `@Bean` | providers: [...] |
| Hilt | Spring DI (@Autowired) | Angular DI (@Injectable) |
| Room | JPA / Hibernate | — |
| Retrofit | WebClient / RestTemplate | HttpClient |
| WorkManager | @Scheduled | — |
| `createFromAsset` | `data.sql` / `import.sql` | — |
