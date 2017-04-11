//JTable이 얹혀질 패널
package book;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.print.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

//원래는 dto 써야 하는데 JTable이 옛날거라 적용이 안되서 그냥 vector씀
public class TablePanel extends JPanel{
	Connection con;
	JTable table;
	JScrollPane scroll;
	TableModel model;
	//vector는 백그라운드로 동기화 제공: 안정성은 있지만, ArrayList에 비해연산속도가 느림-> 상황에 따라 골라쓰기
	Vector list=new Vector(); 
	Vector<String> columnName=new Vector<String>();

	int cols;
	
	
	//얻어와야지 하면 무조건 메서드 전달을 생각!
	public TablePanel() {
		this.con=con;
		//테이블 나오기 전 db 연동 먼저!
	
		
		table=new JTable();
		scroll=new JScrollPane(table);
		this.setLayout(new BorderLayout());
		this.add(scroll);
		
		
		
		this.setBackground(Color.PINK);
		setPreferredSize(new Dimension(650, 550));
	}
	
	//생성자에 넣었을 때 타이밍 문제로 메서드로 따로 만들어줌. 내가 원할 때 호출할 수 있음
	public void setConnection(Connection con){
		this.con=con;
		
		init(); //커넥션을 넘겨받았을 때 실행!
		
		//테이블 모델을 jtable에 적용
		model=new AbstractTableModel() {		
			public int getRowCount() {				
				return list.size();
			}
			
			public int getColumnCount() {
				return cols;
			}
			
			public Object getValueAt(int row, int col) {			
				Vector vec=(Vector)list.get(row); 
				return vec.elementAt(col);
			}
			
		
		};
		//테이블에 모델 적용
		table.setModel(model);
	}
	
	//book 테이블의 레코드 (데이터베이스 가져오기)
	public void init(){
		String sql="select * from book order by book_id asc";
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			cols=rs.getMetaData().getColumnCount();
			
			list.removeAll(list);
			columnName.removeAll(columnName);
			
			while(rs.next()){
				Vector<String> data=new Vector<String>();
				
				data.add(Integer.toString(rs.getInt("book_id")));
				data.add(rs.getString("book_name"));
				data.add(Integer.toString(rs.getInt("price")));
				data.add(rs.getString("img"));
				data.add(Integer.toString(rs.getInt("subcategory_id")));
				
				list.add(data); //기본 벡터에 벡터 추가
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		
		
		
		
		
	}
	
}
