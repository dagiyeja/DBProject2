package oracle;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AppMain extends JFrame implements ItemListener{
	ConnectionManager manager; //1번만 쓸것이므로 
	Connection con; //모든 객체간 공유하기 위해
	JTable table;
	JScrollPane scroll;
	JPanel p_west, p_center;
	Choice choice;
	String[][] item={
			{"▼테이블을 선택하세요", ""} //html처럼 쓰기 편하게  일부러 이차원배열로 작성
			,{"사원테이블","emp"}
			,{"부서테이블","dept"}
	};
	
	TableModel[] model=new TableModel[item.length];
	
	
	public AppMain() {
		//디자인과 로직을 분리시키기 위해 중간자(Controller)
		//의 존재가 필요하다 JTable에서는 이 컨트롤러의 역할을 TableModel이 해준다!
		//TableModel을 사용할 경우, JTable은 자신이 보여줘야할 데이터를 TableModel로부터 정보를 얻어와 출력한다.
		//getColumnCount()
		//getRowCount()
		//getValueAt()
		manager=ConnectionManager.getInstance();
		con=manager.getConnection();
		
		table=new JTable();
		scroll=new JScrollPane(table);
		p_west=new JPanel();
		p_center=new JPanel();
		choice=new Choice();
		
		//테이블 모델들을 올려놓자!!
		model[0]=new DefaultTableModel();
		model[1]=new EmpModel(con);
		model[2]=new DeptModel(con);
		
		//초이스 구성
		for(int i =0; i<item.length; i++){
			choice.add(item[i][0]);
		}
		//west 영역의 패널에 초이스 부착
		p_west.add(choice);
		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		pack();
		
		//초이스와 리스너 연결
		choice.addItemListener(this);
		
		//윈도우창 닫을 때 오라클 접속 끊기
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				//커넥션 닫기
				manager.disConnect(con);
				
				//프로그램 종료
				System.exit(0);
			}
		});
		
		setVisible(true);
		
	}

	//해당되는 테이블 보여주기
	public void showData(int index){
			System.out.println("당신이 보게 될 테이블은 "+item[index][1]);
			table.setModel(model[index]);
			
			//해당 되는 테이블 모델을 사용하면 된다!!
			//emp--> EmpModel
			//dept--> DeptModel
			//아무것도 아니면 -->DefaultTableMode
	}
	
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		
		int index=ch.getSelectedIndex();
		showData(index);
		
		
	}
	
	public static void main(String[] args) {
		new AppMain();

	}



}
