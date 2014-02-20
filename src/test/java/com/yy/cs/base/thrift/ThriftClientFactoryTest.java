package com.yy.cs.base.thrift;



public class ThriftClientFactoryTest {
//	
//	Iface face = null;
//	ThriftClientFactory<Iface> thriftClient;
//	@Before
//    public void before() {
//		 
//		ThriftConfig config = new ThriftConfig("127.0.0.1",8181);
//		thriftClient = new ThriftClientFactory<Iface>();
//		thriftClient.getThriftConfig().add(config);
//		thriftClient.setInterface(Iface.class);
//
//		face = thriftClient.getClient();
//    }
//	/**
//	 * 
//	 */
//    @After
//    public void after() {
//    	thriftClient.destroy();
//    }
//   
//    
//    @Test
//    public void testSpring()  throws TException {
////    	ApplicationContext context = new ClassPathXmlApplicationContext("spring-thrift.xml");
////    	ThriftClientFactory<Iface> c =  (ThriftClientFactory<Iface>) context.getBean("clientFactory");
//    	final Iface f = thriftClient.getClient();
//    	
//    	for(int i = 0; i<1; i++){
//    		new Thread(new Runnable() {  
//                public void run() {  
//                	for(;;){
//                		try {  
//                			Thread.sleep(1000);
//                			f.send("1 ", "2 ", "3 ");
//                			
////	                    	System.out.println();
//                    	} catch (Exception e) {  
//                    		e.printStackTrace();  
//                    	}  
//                    } 
//                }
//            }).start(); 
//    	}
//    	
//    	for(;;){
//    		try {
//        		//System.out.println(f.send("1111111111", "222222222", "222222222"));
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//    	}
//    	
//    }
//    
//    
//    
//    @Test
//    public void test () throws TException {
//    	System.out.println(face.send("1111111111", "222222222", "222222222"));
//    }
}
