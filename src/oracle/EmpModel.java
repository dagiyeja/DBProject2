//emp 테이블의 데이터를 처리하는 컨트롤러!!
package oracle;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
 
import javax.swing.table.AbstractTableModel;
//인터페이스 구현안한다, 너무나 많아서 그중에서 구현한다!!
public class EmpModel extends AbstractTableModel{
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	
	String[] column; //컬럼을 넣을배열!!
	String[][] data; //레코드를 넣을배열!!
	//이차원 배열의 크기는 지금 알수없지!! 테이블에 의해 작성될거야
	
	//1. 생성자에서 디비연동 부터 하자
	public EmpModel(Connection con) {
		this.con=con;
		
		
		/*1. 드라이버로드
		 * 2. 드라이버접속
		 * 3. 쿼리문 작성
		 * 4. 접속 닫기(사용된 자원 모두 닫기)
		 * */
		try {
			//접속완료된거 확인하려면?
			if(con!=null){
			 
			 String sql="select * from emp";
			 //커서가 자유로우면... 스크롤 가능하고.. 읽기가능한..
			 //상수 두개 추가하자
			 //아래의 pstmt 에 의해 생성되는 rs 는 커거사 자유로울수 있다..
			 pstmt=con.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			 rs=pstmt.executeQuery(); //결과집합반환
			 
			 //컬럼을 구해보자!!
			 ResultSetMetaData meta=rs.getMetaData(); //메타라고 하면 뭐든 그 자체
			 //메타데이터는 설정정보
			 int count=meta.getColumnCount(); //컬럼의개수
			 column=new String[count]; //컬럼담을 배열 준비!!
			 
			 //컬럼명을 채우자!!
			 for(int i=0; i<column.length; i++){
				 column[i]=meta.getColumnName(i+1);
				 //meta 라는객체가 컬럼을 반환해준다.. (괄호안에는 index , 근데 1부터시작하니깐 +1)
			 }
			 
			 rs.last(); //제일 마지막으로 보냄
			 int total=rs.getRow(); //레코드번호
			 rs.beforeFirst(); //첫번째로다시오다..
			 
			 //총 레코드 수를 알았으니 , 이차원 배열 생성해보자!!
			 //컬럼도 끝없으니 늘어날수도 있잖아!!
			 data = new String[total][column.length];
			 
			 //레코드를 이차원배열인 data 에 채워넣기..
			
			 for(int a=0; a<data.length; a++){ //층수만큼
				 	rs.next(); //다음다음이동!!
				 for(int i=0; i<data[a].length; i++){ //호수만큼
					 data[a][i]=rs.getString(column[i]);
					 //컬럼명은 column[i]; ....
					 //데이터베이스의 자료형은 일치하지않아도된다
					 
				 }
				 
			 }
			
			 
			}
		}  catch (SQLException e) {
			System.out.println("드라이버 접속 실패");
			e.printStackTrace();
		}finally{
			if(rs!=null){
			  try {
				rs.close();
			} catch (SQLException e) {
				System.out.println("rs 안닫힘");
				e.printStackTrace();
			}
			}
			if(pstmt!=null){
				  try {
					rs.close();
				} catch (SQLException e) {
					System.out.println("pstmt 안닫힘");
					e.printStackTrace();
				}
				}
		
		}
		
	}
	
	@Override
	public int getColumnCount() {
	
		return column.length;
	}
	
	//기본 3가지 메서드 외에 컬럼명을 셋팅해주는 메서드 추가!!
	@Override
	public String getColumnName(int index) {
		
		return column[index];
	}
 
	@Override
	public int getRowCount() {
		
		return data.length;
	}
 
	@Override
	public Object getValueAt(int row, int col) {
		
		return data[row][col];
	}
		
}
