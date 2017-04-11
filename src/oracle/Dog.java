//강아지 클래스의 인스턴스를 오직 1개만 만들기
//Singleton패턴- 개발 패턴 중 하나
/*java SE,
 *java EE 고급기술(java SE를 포함하여 기업용 어플리케이션 제작에 사용됨)
 *
 * 
 * */
package oracle;

public class Dog {
	static private Dog instance;
	
	//new에 의한 생성을 막자!!
	private Dog(){
		
	}
	
	//생성자에 의한 생성은 막고 오직 메서드를 통해 생성하도록 하자
	
	static public Dog getInstance() { //외부에서도 호출 가능
		if(instance==null){
			instance=new Dog();
		}
		return instance;
	}
	
	
}

