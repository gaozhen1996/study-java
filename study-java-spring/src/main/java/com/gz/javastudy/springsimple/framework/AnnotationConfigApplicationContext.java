package com.gz.javastudy.springsimple.framework;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.gz.javastudy.springsimple.framework.util.Console;

public class AnnotationConfigApplicationContext{
	
	public AnnotationConfigApplicationContext() {
		super();
	}
	
	public AnnotationConfigApplicationContext(Class<?> configClass) {
		//1.扫描类
		MyComponentScan componentScan = (MyComponentScan) configClass.getAnnotation(MyComponentScan.class);
		String scanPath = componentScan.value();
		this.doScanner(scanPath);
		refresh();
	}
	
	public void refresh() {
		// 初始化所有实例并放入ioc容器中
		doInstance();

		// 注入依赖（DI）
		doAutowired();
	}

	// 用于存放所有加载了的类的name
	private List<String> className = new ArrayList<String>();

	/**
	 * ioc bean container, you can add element to this map IOC容器，用来存放java bean
	 */
	private Map<String, Object> ioc = new ConcurrentHashMap<String, Object>();


	@SuppressWarnings("unchecked")
	public <T> T getBean(String beanName) {
		if (ioc.containsKey(beanName)) {
			return (T) ioc.get(beanName);
		} else {
			return null;
		}
	}

	/**
	 * 4.DI,依赖注入
	 */
	private void doAutowired() {
		// TODO Auto-generated method stub
		if (ioc.size() == 0)
			return;
		for (Entry<String, Object> entry : ioc.entrySet()) {
			// 拿到实例中的对象属性，然后判断有没有Autowired
			// 有的话就设置值
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for (Field f : fields) {
				if (f.isAnnotationPresent(MyAutowired.class)) {
					String beanName = f.getName();
					f.setAccessible(true);// 设置private
					if (!beanName.equals("")) {
						// 将需要的java bean放入实例对象里的属性值中
						try {
							f.set(entry.getValue(), ioc.get(beanName));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							continue;
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							continue;
						}
					}
				} else {
					continue;
				}
			}
		}
	}

	/**
	 * <p>
	 * 3.通过className的list里面的名字去反射加载类
	 * </p>
	 * <p>
	 * 加载完后，将加了注解{@link MyController}和注解{@link MyService}的注入IOC容器中
	 * </p>
	 */
	private void doInstance() {
		if (className.size() == 0)
			return;
		try {
			for (String name : className) {
				Class<?> clazz = Class.forName(name);
				if (clazz.isAnnotationPresent(MyService.class)) {
					MyService s = clazz.getAnnotation(MyService.class);
					String beanName = s.value();
					Console.info("加载Service");
					// 如果用户设置了Service的名字，就存用户设置的名字
					if (!beanName.equals("")) {
						ioc.put(beanName, clazz.newInstance());
						continue;
					} else {
						ioc.put(lowerFirstStr(clazz.getSimpleName()), clazz.newInstance());
					}
				} else {
					Console.info("其他类：" + name + "，忽略！");
					continue;
				}
			}
		} catch (Exception e) {
			
		}
	}

	private String lowerFirstStr(String str) {
		char[] chars = str.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * 2.扫描文件并加载入className的list
	 * 
	 * @param packageName
	 */
	private void doScanner(String packageName) {
		URL url = AnnotationConfigApplicationContext.class.getResource("/" + packageName.replaceAll("\\.", "/"));
		
		System.out.println(url);
		
		File dir = new File(url.getFile());
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				// 如果是文件夹则继续递归
				doScanner(packageName + "." + f.getName());
			} else {
				// 将包名等信息整理后放入className的list中
				className.add(packageName + "." + f.getName().replaceAll(".class", "").trim());
			}
		}
	}

}
