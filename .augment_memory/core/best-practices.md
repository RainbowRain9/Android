# Android AI日记应用最佳实践

## 代码规范

### Java代码规范
```java
// 类命名：使用PascalCase
public class DiaryRepository {
    
    // 常量：使用UPPER_SNAKE_CASE
    private static final String TAG = "DiaryRepository";
    private static final int MAX_RETRY_COUNT = 3;
    
    // 成员变量：使用camelCase，私有变量可选m前缀
    private DiaryDao diaryDao;
    private ExecutorService executorService;
    
    // 方法命名：使用camelCase，动词开头
    public void getAllDiaries(DataCallback<List<Diary>> callback) {
        // 方法实现
    }
    
    // 参数和局部变量：使用camelCase
    private void processResult(List<Diary> diaries, DataCallback<List<Diary>> callback) {
        // 实现
    }
}
```

### Kotlin代码规范
```kotlin
// 类命名：使用PascalCase
class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {
    
    // 属性命名：使用camelCase
    private val _diaries = MutableLiveData<List<Diary>>()
    val diaries: LiveData<List<Diary>> = _diaries
    
    // 函数命名：使用camelCase
    fun loadDiaries() {
        viewModelScope.launch {
            try {
                val result = repository.getAllDiaries()
                _diaries.value = result
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
    
    // 私有函数
    private fun handleError(error: Exception) {
        // 错误处理
    }
}
```

### 资源命名规范
```xml
<!-- 布局文件：activity_功能名.xml, fragment_功能名.xml -->
activity_diary_editor.xml
fragment_home.xml
item_diary_list.xml

<!-- ID命名：类型_功能描述 -->
android:id="@+id/tv_diary_title"
android:id="@+id/btn_save_diary"
android:id="@+id/rv_diary_list"

<!-- 字符串资源：功能_描述 -->
<string name="diary_title_hint">请输入日记标题</string>
<string name="error_network_unavailable">网络不可用</string>

<!-- 颜色资源：语义化命名 -->
<color name="primary_color">#2196F3</color>
<color name="text_primary">#212121</color>
<color name="background_card">#FFFFFF</color>
```

## 架构最佳实践

### MVVM实现最佳实践
```java
// ViewModel：不持有View引用，使用LiveData通信
public class DiaryEditorViewModel extends ViewModel {
    private MutableLiveData<String> titleLiveData = new MutableLiveData<>();
    private MutableLiveData<String> contentLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> savingLiveData = new MutableLiveData<>();
    
    // 提供只读的LiveData
    public LiveData<String> getTitle() { return titleLiveData; }
    public LiveData<String> getContent() { return contentLiveData; }
    public LiveData<Boolean> isSaving() { return savingLiveData; }
    
    // 业务逻辑方法
    public void updateTitle(String title) {
        titleLiveData.setValue(title);
    }
    
    public void saveDiary() {
        savingLiveData.setValue(true);
        // 保存逻辑
    }
}

// Activity/Fragment：只负责UI逻辑
public class DiaryEditorActivity extends AppCompatActivity {
    private DiaryEditorViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_editor);
        
        viewModel = new ViewModelProvider(this).get(DiaryEditorViewModel.class);
        
        // 观察数据变化
        viewModel.getTitle().observe(this, title -> {
            if (!Objects.equals(titleEditText.getText().toString(), title)) {
                titleEditText.setText(title);
            }
        });
        
        viewModel.isSaving().observe(this, isSaving -> {
            saveButton.setEnabled(!isSaving);
            progressBar.setVisibility(isSaving ? View.VISIBLE : View.GONE);
        });
    }
}
```

### Repository模式最佳实践
```java
// Repository接口：定义数据操作契约
public interface DiaryRepository {
    void getAllDiaries(DataCallback<List<Diary>> callback);
    void getDiaryById(long id, DataCallback<Diary> callback);
    void insertDiary(Diary diary, DataCallback<Long> callback);
    void updateDiary(Diary diary, DataCallback<Void> callback);
    void deleteDiary(long id, DataCallback<Void> callback);
}

// Repository实现：处理数据源逻辑
public class DiaryRepositoryImpl implements DiaryRepository {
    private DiaryDao localDataSource;
    private DiaryApiService remoteDataSource; // 可选
    private ExecutorService executor;
    
    @Override
    public void getAllDiaries(DataCallback<List<Diary>> callback) {
        executor.execute(() -> {
            try {
                // 优先从本地获取
                List<Diary> localDiaries = localDataSource.getAllDiaries();
                
                // 回到主线程
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onSuccess(localDiaries));
                
                // 可选：后台同步远程数据
                syncWithRemoteIfNeeded();
                
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onError(e));
            }
        });
    }
    
    private void syncWithRemoteIfNeeded() {
        // 后台同步逻辑
    }
}
```

## 数据库最佳实践

### Room实体设计
```java
@Entity(tableName = "contents",
        indices = {
            @Index(value = {"user_id", "type"}),
            @Index(value = {"created_at"}),
            @Index(value = {"title", "content"}, name = "idx_search")
        })
public class Content {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @ColumnInfo(name = "user_id")
    public long userId;
    
    @NonNull
    @ColumnInfo(name = "type")
    public String type;
    
    @ColumnInfo(name = "title")
    public String title;
    
    @NonNull
    @ColumnInfo(name = "content")
    public String content;
    
    @ColumnInfo(name = "mood")
    public Integer mood;
    
    @ColumnInfo(name = "created_at")
    public Date createdAt;
    
    @ColumnInfo(name = "updated_at")
    public Date updatedAt;
    
    // 构造函数
    public Content(@NonNull String type, @NonNull String content) {
        this.type = type;
        this.content = content;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}

// 类型转换器
@TypeConverter
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
```

### DAO设计最佳实践
```java
@Dao
public interface ContentDao {
    // 基础CRUD操作
    @Query("SELECT * FROM contents WHERE user_id = :userId ORDER BY created_at DESC")
    List<Content> getContentsByUserId(long userId);
    
    @Query("SELECT * FROM contents WHERE id = :id")
    Content getContentById(long id);
    
    @Insert
    long insertContent(Content content);
    
    @Update
    void updateContent(Content content);
    
    @Delete
    void deleteContent(Content content);
    
    // 复杂查询
    @Query("SELECT * FROM contents WHERE user_id = :userId AND type = :type " +
           "ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    List<Content> getContentsByTypeWithPaging(long userId, String type, int limit, int offset);
    
    @Query("SELECT * FROM contents WHERE user_id = :userId AND " +
           "(title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') " +
           "ORDER BY created_at DESC")
    List<Content> searchContents(long userId, String query);
    
    // 统计查询
    @Query("SELECT COUNT(*) FROM contents WHERE user_id = :userId AND type = :type")
    int getContentCountByType(long userId, String type);
    
    @Query("SELECT type, COUNT(*) as count FROM contents WHERE user_id = :userId GROUP BY type")
    List<ContentTypeCount> getContentCountByTypes(long userId);
}

// 统计结果类
public class ContentTypeCount {
    public String type;
    public int count;
}
```

## UI最佳实践

### Fragment生命周期管理
```java
public abstract class BaseFragment extends Fragment {
    protected boolean isViewCreated = false;
    protected boolean isDataLoaded = false;
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        
        // 初始化UI
        initViews(view);
        
        // 设置监听器
        setupListeners();
        
        // 如果Fragment可见，加载数据
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
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isDataLoaded = false;
    }
    
    // 子类实现的抽象方法
    protected abstract void initViews(View view);
    protected abstract void setupListeners();
    protected abstract void loadData();
}
```

### RecyclerView优化
```java
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {
    private List<Content> contents = new ArrayList<>();
    private OnItemClickListener listener;
    
    // 使用DiffUtil优化更新
    public void updateContents(List<Content> newContents) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
            new ContentDiffCallback(this.contents, newContents));
        
        this.contents.clear();
        this.contents.addAll(newContents);
        diffResult.dispatchUpdatesTo(this);
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
    
    @Override
    public int getItemCount() {
        return contents.size();
    }
    
    // ViewHolder优化
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView contentText;
        private TextView dateText;
        private ImageView moodIcon;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 缓存View引用
            titleText = itemView.findViewById(R.id.tv_title);
            contentText = itemView.findViewById(R.id.tv_content);
            dateText = itemView.findViewById(R.id.tv_date);
            moodIcon = itemView.findViewById(R.id.iv_mood);
            
            // 设置点击监听
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(contents.get(position));
                }
            });
        }
        
        public void bind(Content content) {
            titleText.setText(content.title);
            contentText.setText(content.content);
            dateText.setText(formatDate(content.createdAt));
            
            // 设置心情图标
            if (content.mood != null) {
                moodIcon.setImageResource(getMoodIcon(content.mood));
                moodIcon.setVisibility(View.VISIBLE);
            } else {
                moodIcon.setVisibility(View.GONE);
            }
        }
    }
    
    // DiffUtil回调
    private static class ContentDiffCallback extends DiffUtil.Callback {
        private List<Content> oldList;
        private List<Content> newList;
        
        public ContentDiffCallback(List<Content> oldList, List<Content> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }
        
        @Override
        public int getOldListSize() {
            return oldList.size();
        }
        
        @Override
        public int getNewListSize() {
            return newList.size();
        }
        
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).id == newList.get(newItemPosition).id;
        }
        
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Content oldContent = oldList.get(oldItemPosition);
            Content newContent = newList.get(newItemPosition);
            return Objects.equals(oldContent.title, newContent.title) &&
                   Objects.equals(oldContent.content, newContent.content) &&
                   Objects.equals(oldContent.mood, newContent.mood);
        }
    }
}
```

## 性能优化最佳实践

### 内存优化
```java
public class MemoryOptimizationHelper {
    
    // 使用WeakReference避免内存泄漏
    public static class WeakHandler extends Handler {
        private WeakReference<Activity> activityRef;
        
        public WeakHandler(Activity activity) {
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
    
    // 图片内存优化
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        
        return BitmapFactory.decodeFile(path, options);
    }
    
    private static int calculateInSampleSize(BitmapFactory.Options options, 
                                           int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
}
```

### 数据库性能优化
```java
// 批量操作优化
@Dao
public interface ContentDao {
    @Insert
    List<Long> insertContents(List<Content> contents);
    
    @Update
    void updateContents(List<Content> contents);
    
    @Delete
    void deleteContents(List<Content> contents);
    
    // 使用事务
    @Transaction
    default void replaceContents(List<Content> contents) {
        deleteAllContents();
        insertContents(contents);
    }
    
    @Query("DELETE FROM contents")
    void deleteAllContents();
}

// Repository中的批量操作
public class DiaryRepositoryImpl implements DiaryRepository {
    
    @Override
    public void batchInsertDiaries(List<Diary> diaries, DataCallback<List<Long>> callback) {
        executor.execute(() -> {
            try {
                // 使用事务提高性能
                database.runInTransaction(() -> {
                    List<Long> ids = diaryDao.insertContents(diaries);
                    new Handler(Looper.getMainLooper()).post(() -> 
                        callback.onSuccess(ids));
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onError(e));
            }
        });
    }
}
```

## 错误处理最佳实践

### 统一错误处理
```java
public class ErrorHandler {
    private static final String TAG = "ErrorHandler";
    
    public static void handleError(Context context, Throwable error) {
        Log.e(TAG, "Error occurred", error);
        
        String message = getErrorMessage(context, error);
        showErrorToUser(context, message);
        
        // 可选：上报错误到分析平台
        reportError(error);
    }
    
    private static String getErrorMessage(Context context, Throwable error) {
        if (error instanceof NetworkException) {
            return context.getString(R.string.error_network);
        } else if (error instanceof DatabaseException) {
            return context.getString(R.string.error_database);
        } else if (error instanceof ValidationException) {
            return error.getMessage();
        } else {
            return context.getString(R.string.error_unknown);
        }
    }
    
    private static void showErrorToUser(Context context, String message) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private static void reportError(Throwable error) {
        // 上报到Crashlytics或其他分析平台
    }
}
```

## 测试最佳实践

### 单元测试
```java
@RunWith(JUnit4.class)
public class DiaryRepositoryTest {
    
    @Mock
    private DiaryDao mockDao;
    
    @Mock
    private Context mockContext;
    
    private DiaryRepository repository;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        repository = new DiaryRepositoryImpl(mockDao, mockContext);
    }
    
    @Test
    public void getAllDiaries_Success_ReturnsData() {
        // Given
        List<Diary> expectedDiaries = createTestDiaries();
        when(mockDao.getAllDiaries()).thenReturn(expectedDiaries);
        
        // When
        TestCallback<List<Diary>> callback = new TestCallback<>();
        repository.getAllDiaries(callback);
        
        // Then
        callback.awaitResult();
        assertTrue(callback.isSuccess());
        assertEquals(expectedDiaries, callback.getResult());
    }
    
    @Test
    public void getAllDiaries_DatabaseError_ReturnsError() {
        // Given
        Exception expectedException = new RuntimeException("Database error");
        when(mockDao.getAllDiaries()).thenThrow(expectedException);
        
        // When
        TestCallback<List<Diary>> callback = new TestCallback<>();
        repository.getAllDiaries(callback);
        
        // Then
        callback.awaitResult();
        assertTrue(callback.isError());
        assertEquals(expectedException, callback.getError());
    }
    
    private List<Diary> createTestDiaries() {
        return Arrays.asList(
            new Diary("Title 1", "Content 1"),
            new Diary("Title 2", "Content 2")
        );
    }
}

// 测试用的回调类
class TestCallback<T> implements DataCallback<T> {
    private CountDownLatch latch = new CountDownLatch(1);
    private T result;
    private Exception error;
    private boolean success = false;
    
    @Override
    public void onSuccess(T data) {
        this.result = data;
        this.success = true;
        latch.countDown();
    }
    
    @Override
    public void onError(Exception e) {
        this.error = e;
        this.success = false;
        latch.countDown();
    }
    
    public void awaitResult() {
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public boolean isSuccess() { return success; }
    public boolean isError() { return !success; }
    public T getResult() { return result; }
    public Exception getError() { return error; }
}
```
