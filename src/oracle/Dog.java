//������ Ŭ������ �ν��Ͻ��� ���� 1���� �����
//Singleton����- ���� ���� �� �ϳ�
/*java SE,
 *java EE ��ޱ��(java SE�� �����Ͽ� ����� ���ø����̼� ���ۿ� ����)
 *
 * 
 * */
package oracle;

public class Dog {
	static private Dog instance;
	
	//new�� ���� ������ ����!!
	private Dog(){
		
	}
	
	//�����ڿ� ���� ������ ���� ���� �޼��带 ���� �����ϵ��� ����
	
	static public Dog getInstance() { //�ܺο����� ȣ�� ����
		if(instance==null){
			instance=new Dog();
		}
		return instance;
	}
	
	
}

