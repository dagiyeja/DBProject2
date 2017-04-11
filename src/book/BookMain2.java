package book;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class BookMain2 extends JFrame implements ItemListener{
	DBManager manager=DBManager.getInstance();
	Connection con;
	
	JPanel p_west; //���� �����
	JPanel p_content; //���� ���� ��ü
	JPanel p_north; //���� ���� ��� ����
	JPanel p_table; //JTable�� �ٿ��� �г�
	JPanel p_grid; //�׸��� ������� ������ �г�
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	
	public BookMain2() {
	
		
		p_west=new JPanel();
		p_content=new JPanel();
		p_north=new JPanel();
		p_table=new JPanel();
		p_grid=new JPanel();
		
		ch_top=new Choice();
	
		ch_sub=new Choice();
		
		t_name=new JTextField(12);
		t_price=new JTextField(12);
		
		can=new Canvas();
		bt_regist=new JButton("���");
		
		group=new CheckboxGroup();
		ch_table=new Checkbox("���̺� ����", false, group);
		ch_grid=new Checkbox("grid", false, group);
		
		p_west.setLayout(new FlowLayout());
		
		//���̽� ������Ʈ�� ũ�� ����
		
		ch_top.setPreferredSize(new Dimension(130, 30));
		ch_sub.setPreferredSize(new Dimension(130, 30));
		bt_regist.setPreferredSize(new Dimension(130, 30));
		
		
		
		p_west.setPreferredSize(new Dimension(150, 600));
		
		p_west.setBackground(Color.GRAY);
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can);
		p_west.add(bt_regist);
		
		
		
		p_north.add(ch_table);
		p_north.add(ch_grid);
	
		
		p_content.add(p_north, BorderLayout.NORTH);
		p_content.add(p_table,BorderLayout.CENTER);
		p_content.add(p_grid, BorderLayout.CENTER);
	
		
		
		
		add(p_west, BorderLayout.WEST);
		add(p_content);
		
		init();
		ch_top.addItemListener(this);
		
		setSize(800,600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	}
	
	public void init(){
		//���̽� ������Ʈ�� �ֻ��� ��� ���̱�!!
		con=manager.getConnection();
		String sql="select * from topcategory";
		PreparedStatement pstmt=null; //try���� ������ �������ϱ� 
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				ch_top.add(rs.getString("category_name"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//���� ī�װ� ��������
	public void getSub(String v){
		//������ �̹� ä���� �������� �ִٸ�, ���� �� �����!
		ch_sub.removeAll();
		
		StringBuffer sb=new StringBuffer();
		sb.append("select * from subcategory");
		sb.append(" where topcategory_id=("); //��ĭ �� ����
		sb.append(" select topcategory_id from");
		sb.append(" topcategory where category_name='"+v+"')");
		
		System.out.println(sb.toString()); //�������� �� ����Ǵ��� Ȯ��
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				ch_sub.add(rs.getString("category_name"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		getSub(ch.getSelectedItem());
		
	}
	
	public static void main(String[] args) {
		new BookMain2();

	}

}
