# Architecture & Component Guide

## 🏗️ Project Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│                    (User Interface)                          │
├─────────────────────────────────────────────────────────────┤
│  MainActivity      DashboardActivity   AddStudentActivity   │
│  (Login/Register)      (Main List)      (Add/Edit Form)     │
│                                                              │
│         StudentDetailsActivity   (View Profile)             │
│                                                              │
│  ┌────────────────────────────────────────────────────┐    │
│  │         UI Components (Material Design 3)          │    │
│  ├────────────────────────────────────────────────────┤    │
│  │  • MaterialToolbar       • MaterialCardView         │    │
│  │  • TextInputLayout       • MaterialButton          │    │
│  │  • RecyclerView          • SwipeRefreshLayout      │    │
│  │  • MaterialAlertDialog   • ProgressBar            │    │
│  └────────────────────────────────────────────────────┘    │
│                                                              │
│  StudentAdapter (RecyclerView.Adapter<StudentViewHolder>)  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER                      │
├─────────────────────────────────────────────────────────────┤
│                 Validation & Helper Classes                  │
│  ┌──────────────────────────────────────────────────┐       │
│  │       ValidationUtils                            │       │
│  │  • isValidEmail()                                │       │
│  │  • isValidPassword()                             │       │
│  │  • isValidStudentName()                          │       │
│  │  • isValidCourse()                               │       │
│  │  • getErrorMessage()                             │       │
│  └──────────────────────────────────────────────────┘       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                     DATA LAYER                               │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐        ┌──────────────────┐           │
│  │  Room Database   │        │   API Service    │           │
│  ├──────────────────┤        ├──────────────────┤           │
│  │                  │        │                  │           │
│  │ StudentDatabase  │        │  Retrofit        │           │
│  │  ↓               │        │  ↓               │           │
│  │ StudentDao       │        │ ApiService       │           │
│  │  ↓               │        │ RetrofitClient   │           │
│  │ Student Entity   │        │                  │           │
│  │  (Local Store)   │        │ JSONPlaceholder  │           │
│  │                  │        │ (Remote API)     │           │
│  └──────────────────┘        └──────────────────┘           │
│                                                              │
│  ┌──────────────────────────────────────────────────┐       │
│  │              Models                              │       │
│  ├──────────────────────────────────────────────────┤       │
│  │ • Student.java (@Entity, @Room)                  │       │
│  │   - id, name, rollNumber, email, course          │       │
│  │   - isFavorite, isFromApi                        │       │
│  │                                                  │       │
│  │ • User.java (API model)                          │       │
│  │   - id, name, email, phone, website              │       │
│  └──────────────────────────────────────────────────┘       │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow Architecture

### Adding a Student:
```
AddStudentActivity
    ↓
User fills form
    ↓
ValidationUtils.validate()
    ↓
Save to Database (Async)
    ↓
ExecutorService.execute()
    ↓
StudentDao.insertStudent(student)
    ↓
Room writes to SQLite
    ↓
runOnUiThread → Toast "Saved"
    ↓
finish() → Back to Dashboard
    ↓
onResume() → refreshData()
```

### Loading Students:
```
DashboardActivity.onCreate()
    ↓
refreshData()
    ├─→ loadLocalStudents()
    │   ├─→ ExecutorService.execute()
    │   ├─→ studentDao.getAllLocalStudents()
    │   └─→ runOnUiThread → update UI
    │
    └─→ loadApiStudents()
        ├─→ RetrofitClient.getApiService()
        ├─→ apiService.getUsers().enqueue()
        └─→ onResponse() → convert to Student
            └─→ runOnUiThread → update UI
    ↓
All loaded → tryPublishStudents()
    ↓
Combine local + API students
    ↓
studentAdapter.submitList()
    ↓
RecyclerView renders list
```

### Searching Students:
```
User types in search box
    ↓
TextWatcher.onTextChanged()
    ↓
filterStudents(query)
    ↓
Loop through allStudents
    ├─→ Check name.contains(query)
    ├─→ Check email.contains(query)
    ├─→ Check course.contains(query)
    └─→ Check rollNumber.contains(query)
    ↓
Collect matches in filtered list
    ↓
studentAdapter.submitList(filtered)
    ↓
RecyclerView updates with results
```

---

## 📦 Package Structure

```
com.mudesuraj.studentmanagementapp/
│
├── activities/                      [User Interface Layer]
│   ├── MainActivity.java            [Login & Register]
│   ├── DashboardActivity.java       [Main List & Search]
│   ├── AddStudentActivity.java      [Add/Edit Form]
│   └── StudentDetailsActivity.java  [View Details]
│
├── adapters/                        [Presentation Logic]
│   └── StudentAdapter.java          [RecyclerView Adapter]
│       └── StudentViewHolder        [Item ViewHolder]
│
├── models/                          [Data Models]
│   ├── Student.java                 [Local Entity @Room]
│   └── User.java                    [API Model]
│
├── network/                         [API Communication]
│   ├── ApiService.java              [Retrofit Interface]
│   └── RetrofitClient.java          [Retrofit Singleton]
│
├── database/                        [Local Persistence]
│   ├── StudentDatabase.java         [Room Database]
│   └── StudentDao.java              [Data Access Object]
│       ├── insertStudent()
│       ├── updateStudent()
│       ├── deleteStudent()
│       ├── getAllLocalStudents()
│       ├── searchStudents()
│       ├── getFavoriteStudents()
│       ├── getStudentById()
│       └── getLocalStudentCount()
│
├── utils/                           [Helper Classes]
│   └── ValidationUtils.java         [Input Validation]
│
└── R.java (Generated)               [Resources]
    ├── R.layout                     [XML Layouts]
    ├── R.string                     [String Resources]
    ├── R.color                      [Color Palette]
    └── R.drawable                   [Icons/Images]
```

---

## 🔐 Class Responsibilities

### Activities:
| Class | Responsibility |
|-------|-----------------|
| `MainActivity` | Handle login/registration authentication |
| `DashboardActivity` | Display student list, search, refresh |
| `AddStudentActivity` | Add new or edit existing student |
| `StudentDetailsActivity` | View full student profile |

### Adapter:
| Class | Responsibility |
|-------|-----------------|
| `StudentAdapter` | Convert student list to RecyclerView items |
| `StudentViewHolder` | Bind data to individual list item views |

### Models:
| Class | Responsibility |
|-------|-----------------|
| `Student` | Represent student entity (local + API) |
| `User` | Represent API user response |

### Network:
| Class | Responsibility |
|-------|-----------------|
| `ApiService` | Define API endpoints (Retrofit interface) |
| `RetrofitClient` | Configure & provide Retrofit singleton |

### Database:
| Class | Responsibility |
|-------|-----------------|
| `StudentDatabase` | Room database configuration |
| `StudentDao` | All database CRUD operations |

### Utils:
| Class | Responsibility |
|-------|-----------------|
| `ValidationUtils` | Validate user inputs |

---

## 🔌 Dependency Flow

```
MainActivity
├─ FirebaseAuth (Firebase library)
├─ ValidationUtils (local package)
├─ DashboardActivity (next screen)
└─ R.layout, R.string (resources)

DashboardActivity
├─ StudentDatabase (local package)
├─ RetrofitClient → ApiService (local + network)
├─ StudentAdapter (local package)
├─ SwipeRefreshLayout (AndroidX)
├─ RecyclerView (AndroidX)
├─ ExecutorService (Java)
└─ R.layout, R.string (resources)

AddStudentActivity
├─ StudentDatabase (local package)
├─ ValidationUtils (local package)
├─ ExecutorService (Java)
└─ R.layout, R.string (resources)

StudentDetailsActivity
├─ (No additional dependencies)
└─ R.layout, R.string (resources)

StudentAdapter
├─ RecyclerView (AndroidX)
├─ Student model (local package)
└─ R.layout, R.id (resources)

StudentDatabase
├─ Room Library (AndroidX)
├─ Student model (local package)
└─ StudentDao (interface)

StudentDao
├─ Room Library (annotations)
└─ Student model (local package)

RetrofitClient
├─ Retrofit (com.squareup.retrofit2)
├─ OkHttp (com.squareup.okhttp3)
├─ Gson (com.squareup.retrofit2:converter-gson)
└─ ApiService (local package)

ApiService
├─ Retrofit (com.squareup.retrofit2)
└─ User model (local package)

ValidationUtils
├─ Android Patterns (android.util)
└─ (No other dependencies)
```

---

## 🧵 Threading Model

```
┌─────────────────────────────────────────┐
│         MAIN THREAD (UI Thread)         │
├─────────────────────────────────────────┤
│  • Activity UI setup                    │
│  • RecyclerView rendering               │
│  • Toast messages                       │
│  • Dialog display                       │
│  • Button clicks                        │
│  • Search input                         │
└─────────────────────────────────────────┘
         ↓ (UI events)                  ↑ (runOnUiThread)
         
┌─────────────────────────────────────────┐
│    EXECUTOR SERVICE (Background)        │
├─────────────────────────────────────────┤
│  • Database queries (CPU bound)         │
│  • Long operations (non-blocking)       │
│  • Async save/update/delete             │
│                                         │
│  databaseExecutor.execute(() -> {       │
│    // DB operation here                 │
│    runOnUiThread(() -> {                │
│      // Update UI safely                │
│    });                                  │
│  });                                    │
└─────────────────────────────────────────┘
         ↓ (Async call)
         
┌─────────────────────────────────────────┐
│     RETROFIT CALLBACK (Network)         │
├─────────────────────────────────────────┤
│  • API calls                            │
│  • Network I/O (blocking internally)    │
│  • Response parsing                     │
│                                         │
│  apiService.getUsers()                  │
│    .enqueue(new Callback<>() {          │
│      onResponse() → runOnUiThread()     │
│      onFailure() → runOnUiThread()      │
│    });                                  │
└─────────────────────────────────────────┘
```

---

## 📊 Database Schema

### Table: `students`

```sql
CREATE TABLE students (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    
    name TEXT NOT NULL,
    rollNumber TEXT NOT NULL,
    email TEXT NOT NULL,
    course TEXT NOT NULL,
    
    isFavorite BOOLEAN DEFAULT 0,
    isFromApi BOOLEAN DEFAULT 0
)
```

### Queries Used:

```sql
-- Insert new student
INSERT INTO students (name, rollNumber, email, course, isFavorite, isFromApi)
VALUES (?, ?, ?, ?, 0, 0)

-- Get all local students
SELECT * FROM students 
WHERE isFromApi = 0 
ORDER BY id DESC

-- Search students
SELECT * FROM students 
WHERE isFromApi = 0 
AND (name LIKE ?
     OR email LIKE ?
     OR rollNumber LIKE ?
     OR course LIKE ?)

-- Get favorites
SELECT * FROM students WHERE isFavorite = 1

-- Update student
UPDATE students SET name=?, rollNumber=?, email=?, course=?
WHERE id = ?

-- Delete student
DELETE FROM students WHERE id = ?

-- Count local students
SELECT COUNT(*) FROM students WHERE isFromApi = 0
```

---

## 🔄 Activity Lifecycle Integration

```
┌─────────────────────────────────────────┐
│         MainActivity onCreate           │
├─────────────────────────────────────────┤
│  1. Check if user already logged in     │
│  2. If yes → Auto-login → Dashboard    │
│  3. If no → Show login form            │
└─────────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────┐
│      DashboardActivity onCreate         │
├─────────────────────────────────────────┤
│  1. Initialize StudentDatabase          │
│  2. Setup RecyclerView + Adapter       │
│  3. Setup search listener               │
│  4. Setup swipe refresh listener       │
│  5. Call refreshData()                  │
└─────────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────┐
│    DashboardActivity onResume           │
├─────────────────────────────────────────┤
│  → Refresh data each time screen shown |
│  → Loads changes from Add/Edit          │
└─────────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────┐
│   DashboardActivity onDestroy           │
├─────────────────────────────────────────┤
│  → Cleanup ExecutorService              │
│  → Prevent memory leaks                 │
└─────────────────────────────────────────┘
```

---

## 💾 Data Persistence Flow

```
┌──────────────────────────────────┐
│  In-Memory Lists:                │
│  • localStudents                 │
│  • apiStudents                   │
│  • allStudents (combined)        │
└──────────────────────────────────┘
         ↑                    ↓
         │ (UI Updates)  (Display)
         │
┌──────────────────────────────────┐
│  StudentAdapter                  │
│  (RecyclerView connection)       │
└──────────────────────────────────┘
         ↑                    ↓
         │             (Render)
         │
┌──────────────────────────────────┐
│  Room Database                   │
│  (Persistent Storage)            │
│  ├─ insertStudent()              │
│  ├─ updateStudent()              │
│  ├─ deleteStudent()              │
│  └─ getAllLocalStudents()        │
└──────────────────────────────────┘
         ↓
┌──────────────────────────────────┐
│  SQLite Database File            │
│  (Physical Storage)              │
│  📁 /data/data/[package]/        │
│      databases/                  │
│      student_management_db.db    │
└──────────────────────────────────┘
```

---

## 🎯 Feature Architecture

### Authentication:
```
Login/Register Form
    ↓
ValidationUtils.isValidEmail()
ValidationUtils.isValidPassword()
    ↓
FirebaseAuth.signInWithEmailAndPassword()
    ↓
Success → Auto-save session (Firebase)
    ↓
Next launch → Check FirebaseAuth.getCurrentUser()
    ↓
If exists → Auto-login (jump to Dashboard)
If null → Show login form
```

### Search & Filter:
```
User types in SearchBox
    ↓
TextWatcher.onTextChanged(query)
    ↓
filterStudents(query)
    ├─→ For each student in allStudents
    ├─→ Check all fields (name, email, roll, course)
    ├─→ If any field matches query → add to result
    └─→ Collect filtered list
    ↓
studentAdapter.submitList(filtered)
    ↓
RecyclerView.notifyDataSetChanged()
    ↓
UI updates with results
```

### Pull-to-Refresh:
```
User swipes down
    ↓
SwipeRefreshLayout.onRefreshListener
    ↓
refreshData()
    ├─→ loadLocalStudents()
    └─→ loadApiStudents()
    ↓
When all data loaded → setLoadingState(false)
    ↓
SwipeRefreshLayout stops animating
    ↓
New data displayed in RecyclerView
```

---

## 🔌 API Integration Architecture

```
┌────────────────────────────────────┐
│     Retrofit Configuration         │
├────────────────────────────────────┤
│  • Base URL: JSONPlaceholder       │
│  • Converter: GsonConverterFactory │
│  • Interceptor: HttpLoggingInt.    │
│  • Client: OkHttpClient            │
└────────────────────────────────────┘
         ↓
┌────────────────────────────────────┐
│     ApiService Interface           │
├────────────────────────────────────┤
│  @GET("users")                     │
│  Call<List<User>> getUsers()       │
└────────────────────────────────────┘
         ↓
┌────────────────────────────────────┐
│     User Model (API Response)      │
├────────────────────────────────────┤
│  id, name, email, phone, website   │
└────────────────────────────────────┘
         ↓
┌────────────────────────────────────┐
│  Convert to Student Model          │
├────────────────────────────────────┤
│  Map fields + setFromApi(true)     │
└────────────────────────────────────┘
         ↓
┌────────────────────────────────────┐
│  Display in RecyclerView           │
├────────────────────────────────────┤
│  Mark as "API Users (Read Only)"   │
└────────────────────────────────────┘
```

---

## 🎓 Learning Value

This architecture demonstrates:

1. **MVVM-Lite Pattern** - Activities as controllers
2. **DAO Pattern** - Database abstraction
3. **Adapter Pattern** - RecyclerView
4. **Singleton Pattern** - Database & Retrofit
5. **Repository Pattern** - (Simplified) Data source abstraction
6. **Callback Pattern** - Retrofit callbacks
7. **Observer Pattern** - TextWatcher
8. **Executor Service** - Thread management

---

## 📈 Scalability Notes

To extend this architecture:
- Add ViewModel for lifecycle management
- Create Repository layer for abstraction
- Implement LiveData for reactive updates
- Add WorkManager for background tasks
- Use Dependency Injection (Hilt/Dagger)

For now, it's kept **beginner-friendly and easy to understand**!


