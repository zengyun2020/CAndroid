
# CAndroid
一款由crazychen开发的android开发框架，由依赖注入，网络请求，orm框架等部分组成。用于加速android快速开发，目前还在不断完善中。

ViewUtil
这个注解库是使用的编译期依赖注入，所以几乎不会带来反射造成性能损失
目前实现了
contentView,onClick(各种事件),viewById等注解功能
使用时，要将Annotation Processing勾等选上（怎么使用编译器注解功能，可以自行百度或者查看我的csdn博客）
对每个activity都要在oncreate方法里面调用ViewUtils.inject(this)，所以建议写在一个BaseActivity里面
另外目前只实现了activity内的注解功能，view和fragment尚未实现。

//设置布局文件
	@ContentView(R.layout.main)
	public class SecondActivity extends Activity{
		//自动findviewbyid
		@ViewById(R.id.insert_btn)
		Button insert_btn;	
		//添加事件
		@OnClick(R.id.insert_btn)
		void insert(){
		}
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//必须在使用控件前加入这一句
			ViewUtils.inject(this);
		}
	.....
	
	
	
	
DBUtil
DbUtil是ORM框架，可以直接保存bean对象，对于bean对象要求如下
必须在类声明上添加@Table注解，必须为某个int类型添加@Id注解作为主键，bean类必须具有无参数的构造函数

@Table(name="testtable")
public class Person {
	@Id
	public int id = 1;
	private String name;
	
	public Person() {
		// TODO Auto-generated constructor stub
	}
	public Person(int id,String name) {
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return id+":my name is "+name+"\n";
	}
}
该框架支持增删改查四种操作，自动事务，同时有更多的简便接口供大家使用，还支持连贯操作
	db.insert(new Person(0,"sss"));
	db.where("name=\"sss\"").update(new Person(count++,"aaa"));
	db.where("id="+count++).delete(Class.forName("com.crazychen.candroid.Person"));
	System.out.println(db.where("name=\"sss\"").oderBy("id desc").select(Class.forName("com.crazychen.candroid.Person")));			该框架暂不支持复合注解(也就是bean类里面有bean类)，外键等，同时不建议在多线程的情况下同时操作一个DbUtil对象		


HttpUtil
实现大文件下载,图像下载,字符串数据请求等功能，对图像实现了内存缓存和硬盘缓存，避免OOM错误。
使用方式与volley类似，有StringRequest,ImageRequest,FileRequest三种，以后还会陆续增加
		final RequestQueue queue = new RequestQueue(1, null);				
		queue.start();
		Request<String> req = new StringRequest(Request.HttpMethod.GET,
						"http://www.tuicool.com/articles/QRzyeiB",
						new Listener<String>() {
					
					@Override
					public void onSuccess(int stCode, String response, String errMsg) {	
						System.out.println("ss");
						System.out.println(stCode);
						System.out.println(response);
						System.out.println(errMsg);
					}
				});
