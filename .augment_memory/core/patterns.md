# Android AI日记应用开发模式

## 架构模式

### MVVM模式实现
```java
// ViewModel示例
public class DiaryViewModel extends ViewModel {
    private MutableLiveData<List<Diary>> diariesLiveData = new MutableLiveData<>();
    private DiaryRepository repository;
    
    public DiaryViewModel(DiaryRepository repository) {
        this.repository = repository;
    }
    
    public LiveData<List<Diary>> getDiaries() {
        return diariesLiveData;
    }
    
    public void loadDiaries() {
        // 异步加载数据
        repository.getAllDiaries(new DataCallback<List<Diary>>() {
            @Override
            public void onSuccess(List<Diary> diaries) {
                diariesLiveData.setValue(diaries);
            }
            
            @Override
            public void onError(Exception e) {
                // 处理错误
            }
        });
    }
}
```

### Repository模式实现
```java
// Repository接口
public interface DiaryRepository {
    void getAllDiaries(DataCallback<List<Diary>> callback);
    void insertDiary(Diary diary, DataCallback<Long> callback);
    void updateDiary(Diary diary, DataCallback<Void> callback);
    void deleteDiary(long diaryId, DataCallback<Void> callback);
}

// Repository实现
public class DiaryRepositoryImpl implements DiaryRepository {
    private DiaryDao diaryDao;
    private ExecutorService executor;
    
    public DiaryRepositoryImpl(DiaryDao diaryDao) {
        this.diaryDao = diaryDao;
        this.executor = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public void getAllDiaries(DataCallback<List<Diary>> callback) {
        executor.execute(() -> {
            try {
                List<Diary> diaries = diaryDao.getAllDiaries();
                // 回到主线程
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onSuccess(diaries));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onError(e));
            }
        });
    }
}
```

## 数据库模式

### Room实体设计模式
```java
// 基础实体模式
@Entity(tableName = "contents")
public class Content {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @ColumnInfo(name = "user_id")
    public long userId;
    
    @ColumnInfo(name = "type")
    public String type; // diary, note, task, contact, goal, travel
    
    @ColumnInfo(name = "title")
    public String title;
    
    @ColumnInfo(name = "content")
    public String content;
    
    @ColumnInfo(name = "mood")
    public Integer mood; // 1-5级心情
    
    @ColumnInfo(name = "created_at")
    public Date createdAt;
    
    @ColumnInfo(name = "updated_at")
    public Date updatedAt;
    
    // 构造函数、getter、setter
}

// DAO设计模式
@Dao
public interface ContentDao {
    @Query("SELECT * FROM contents WHERE user_id = :userId ORDER BY created_at DESC")
    List<Content> getContentsByUserId(long userId);
    
    @Query("SELECT * FROM contents WHERE user_id = :userId AND type = :type ORDER BY created_at DESC")
    List<Content> getContentsByUserIdAndType(long userId, String type);
    
    @Insert
    long insertContent(Content content);
    
    @Update
    void updateContent(Content content);
    
    @Delete
    void deleteContent(Content content);
    
    @Query("SELECT * FROM contents WHERE user_id = :userId AND (title LIKE :query OR content LIKE :query)")
    List<Content> searchContents(long userId, String query);
}
```

### 数据库迁移模式
```java
// 数据库版本管理
@Database(
    entities = {User.class, Content.class, Tag.class, Attachment.class},
    version = 2,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    // 数据库迁移
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 添加新表
            database.execSQL("CREATE TABLE IF NOT EXISTS `contents` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`user_id` INTEGER NOT NULL, " +
                "`type` TEXT NOT NULL, " +
                "`title` TEXT, " +
                "`content` TEXT NOT NULL, " +
                "`mood` INTEGER, " +
                "`created_at` INTEGER, " +
                "`updated_at` INTEGER)");
        }
    };
    
    public static AppDatabase getInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "app_database")
            .addMigrations(MIGRATION_1_2)
            .build();
    }
}
```

## UI模式

### Fragment生命周期管理模式
```java
public class BaseFragment extends Fragment {
    protected boolean isViewCreated = false;
    protected boolean isDataLoaded = false;
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        initViews(view);
        if (getUserVisibleHint()) {
            lazyLoadData();
        }
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isDataLoaded) {
            lazyLoadData();
        }
    }
    
    protected void lazyLoadData() {
        if (!isDataLoaded) {
            loadData();
            isDataLoaded = true;
        }
    }
    
    protected abstract void initViews(View view);
    protected abstract void loadData();
}
```

### RecyclerView适配器模式
```java
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private List<Content> contents = new ArrayList<>();
    private OnItemClickListener listener;
    
    public interface OnItemClickListener {
        void onItemClick(Content content);
        void onItemLongClick(Content content);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_content, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Content content = contents.get(position);
        holder.bind(content);
    }
    
    public void updateContents(List<Content> newContents) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new ContentDiffCallback(this.contents, newContents));
        this.contents.clear();
        this.contents.addAll(newContents);
        diffResult.dispatchUpdatesTo(this);
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView contentText;
        private TextView dateText;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tv_title);
            contentText = itemView.findViewById(R.id.tv_content);
            dateText = itemView.findViewById(R.id.tv_date);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(contents.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(Content content) {
            titleText.setText(content.title);
            contentText.setText(content.content);
            dateText.setText(formatDate(content.createdAt));
        }
    }
}
```

## 异步处理模式

### 回调接口模式
```java
public interface DataCallback<T> {
    void onSuccess(T data);
    void onError(Exception e);
}

// 使用示例
public class DataManager {
    public void loadData(DataCallback<List<Content>> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // 模拟数据加载
                List<Content> data = loadDataFromDatabase();
                
                // 回到主线程
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onSuccess(data));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onError(e));
            }
        });
    }
}
```

### AsyncTask替代模式
```java
public class AsyncTaskHelper {
    public static <T> void executeAsync(
            Callable<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Exception> onError) {
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            try {
                T result = backgroundTask.call();
                mainHandler.post(() -> onSuccess.accept(result));
            } catch (Exception e) {
                mainHandler.post(() -> onError.accept(e));
            }
        });
    }
}

// 使用示例
AsyncTaskHelper.executeAsync(
    () -> repository.getAllDiaries(), // 后台任务
    diaries -> updateUI(diaries),     // 成功回调
    error -> showError(error)         // 错误回调
);
```

## 错误处理模式

### 统一错误处理
```java
public class ErrorHandler {
    public static void handleError(Context context, Exception e) {
        if (e instanceof NetworkException) {
            showToast(context, "网络连接失败，请检查网络设置");
        } else if (e instanceof DatabaseException) {
            showToast(context, "数据加载失败，请重试");
        } else if (e instanceof ValidationException) {
            showToast(context, e.getMessage());
        } else {
            showToast(context, "操作失败，请重试");
            Log.e("ErrorHandler", "Unexpected error", e);
        }
    }
    
    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
```

### 资源管理模式
```java
public class ResourceManager {
    public static void safeClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    Log.w("ResourceManager", "Failed to close resource", e);
                }
            }
        }
    }
    
    public static <T> T executeWithResource(
            Supplier<T> resourceSupplier,
            Function<T, Object> operation) {
        T resource = null;
        try {
            resource = resourceSupplier.get();
            return (T) operation.apply(resource);
        } finally {
            if (resource instanceof Closeable) {
                safeClose((Closeable) resource);
            }
        }
    }
}
```

## 配置管理模式

### SharedPreferences封装
```java
public class PreferenceManager {
    private static final String PREF_NAME = "app_preferences";
    private SharedPreferences preferences;
    
    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void setString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }
    
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }
    
    public void setBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
    
    // 用户设置相关
    public void setUserId(long userId) {
        preferences.edit().putLong("user_id", userId).apply();
    }
    
    public long getUserId() {
        return preferences.getLong("user_id", -1);
    }
    
    public void setThemeMode(String themeMode) {
        setString("theme_mode", themeMode);
    }
    
    public String getThemeMode() {
        return getString("theme_mode", "system");
    }
}
```

## 测试模式

### 单元测试模式
```java
@RunWith(JUnit4.class)
public class DiaryRepositoryTest {
    
    @Mock
    private DiaryDao mockDao;
    
    private DiaryRepository repository;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        repository = new DiaryRepositoryImpl(mockDao);
    }
    
    @Test
    public void testGetAllDiaries_Success() {
        // Given
        List<Diary> expectedDiaries = Arrays.asList(
            new Diary("Title 1", "Content 1"),
            new Diary("Title 2", "Content 2")
        );
        when(mockDao.getAllDiaries()).thenReturn(expectedDiaries);
        
        // When
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<List<Diary>> result = new AtomicReference<>();
        
        repository.getAllDiaries(new DataCallback<List<Diary>>() {
            @Override
            public void onSuccess(List<Diary> diaries) {
                result.set(diaries);
                latch.countDown();
            }
            
            @Override
            public void onError(Exception e) {
                latch.countDown();
            }
        });
        
        // Then
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }
        
        assertEquals(expectedDiaries, result.get());
    }
}
```

## 性能优化模式

### 图片加载优化
```java
public class ImageLoader {
    private static final int MAX_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private LruCache<String, Bitmap> memoryCache;
    
    public ImageLoader() {
        memoryCache = new LruCache<String, Bitmap>(MAX_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    
    public void loadImage(String url, ImageView imageView) {
        // 先从缓存获取
        Bitmap cached = memoryCache.get(url);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }
        
        // 异步加载
        AsyncTaskHelper.executeAsync(
            () -> loadBitmapFromUrl(url),
            bitmap -> {
                memoryCache.put(url, bitmap);
                imageView.setImageBitmap(bitmap);
            },
            error -> imageView.setImageResource(R.drawable.placeholder)
        );
    }
    
    private Bitmap loadBitmapFromUrl(String url) throws Exception {
        // 实现图片加载逻辑
        return null;
    }
}
```

### 内存泄漏防护模式
```java
public class WeakReferenceHandler extends Handler {
    private WeakReference<Activity> activityRef;
    
    public WeakReferenceHandler(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }
    
    @Override
    public void handleMessage(Message msg) {
        Activity activity = activityRef.get();
        if (activity != null && !activity.isFinishing()) {
            // 处理消息
        }
    }
}
```
