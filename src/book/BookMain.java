package book;
 
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
public class BookMain extends JFrame implements ItemListener, ActionListener{
	Connection con;
	DBManager manager=DBManager.getInstance();
	PreparedStatement pstmt;
	ResultSet rs;
	
	JPanel p_west; //좌측 등록폼
	JPanel p_content; //우측 영역 전체
	JPanel p_north; //우측 선택 모드 영역
	JPanel p_center; //Flow가 적용되어 p_table, p_grid 를 모두 존재시켜 놓을 컨테이너 역할!!
	JPanel p_table; //JTable 이 붙여질 패널
	JPanel p_grid; //그리드 방식으로 보여질 패널
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	Toolkit kit=Toolkit.getDefaultToolkit(); //이것도 일종의 singleTon패턴
	Image img;
	JFileChooser chooser;
	File file;

	
	//html option과는 다르므로, Choice 컴포넌트의 값을 미리 받아놓자!!
	//이 컬렉션은  rs 객체를 대체할 것이다
	//장점? 더이상 rs.last, rs.getRow로 고생하지 말자!
	ArrayList<SubCategory> subcategory=new ArrayList<SubCategory>();
	
	public BookMain() {
		setLayout(new BorderLayout());
		
		p_west = new JPanel();
		p_content = new JPanel();
		p_north = new JPanel();
		p_center= new JPanel();
		
		p_table = new TablePanel();
		p_grid = new GridPanel();
		
		ch_top = new Choice();
		ch_sub = new Choice();
		
		t_name = new JTextField(12);
		t_price = new JTextField(12);
		
		URL url=this.getClass().getResource("/white.gif");
		try {
			img=	ImageIO.read(url); //ToolKit을 써야 하지만, 프로그램 내에 존재하는 이미지를 쓸 때 ImageIO가능
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		can = new Canvas(){
			public void paint(Graphics g) {
				g.drawImage(img,0, 0, 140, 140, this);
			}
		};
		can.setPreferredSize(new Dimension(150, 150));
		
		bt_regist = new JButton("등록");
		
		group = new CheckboxGroup();
		ch_table = new Checkbox("JTable",group,false);
		ch_grid = new Checkbox("Grid",group,false);
		
		//파일 추저 올리기
		chooser=new JFileChooser("C:/html_workspace/images");
		
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));
		//ch_top.add("▼상위카테고리");//상위는 맨처음 무언가가 채워져 있어야하고,
		//ch_sub.add("▼하위카테고리");//하위는 상위가 선택된다음에..
		
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can);
		p_west.add(bt_regist);
		p_west.setBackground(Color.WHITE);
		p_west.setPreferredSize(new Dimension(150, 600));
		add(p_west,BorderLayout.WEST);
		
		//컨텐츠 북쪽
		p_north.add(ch_table);
		p_north.add(ch_grid);
		
		
		p_center.setBackground(Color.YELLOW);
		p_center.add(p_table);
		p_center.add(p_grid);
		
		//p_north.setPreferredSize(new Dimension(650, 100));
		p_content.add(p_north,BorderLayout.NORTH);
		p_content.add(p_center);
		p_content.setPreferredSize(new Dimension(400, 600));
		add(p_content);
		
		init();
		ch_top.addItemListener(this);
		can.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				openFile();
				
			}
			
		});
		
		bt_regist.addActionListener(this);
		
		//초이스 컴포넌트와 리스너 연결
		ch_table.addItemListener(this);
		ch_grid.addItemListener(this);
		
		
		setVisible(true);
		setSize(800,600);
		//setPreferredSize(new Dimension(600,600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	}
	
	private void init() {
		//초이스 컴포넌트에 최상위 목록 보이기!!
		con=manager.getConnection();
		String sql="select * from topcategory order by topcategory_id asc";
		
		try {
			
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				ch_top.add(rs.getString("category_name"));
			} //굳이 커서를 움직일 필요가없다..
			//커서를 자유롭게 움직여야 하는 이유는 배열의 크기를 만들기 위해서였다!!
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					System.out.println("rs 끊기 실패");
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println("pstmt 끊기 실패");
					e.printStackTrace();
				}
			}
		}
		
		//테이블 패널과 그리드 패널에게 Connection 전달
		((TablePanel)p_table).setConnection(con); //p_table의 자료형이 JPanel이므로 자식과 같은 TablePanel형으로 변신
		((GridPanel)p_grid).setConnection(con);
	}
	
	//하위 카테고리 가져오기!!
	public void getSub(String v){
		//기존에 이미 채워진 아이템이 있다면, 먼저 싹 지운다!!
		ch_sub.removeAll();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from subcategory ");
		sb.append(" where topcategory_id=(");
		//하나 더 붙히기전에 한칸 뛴거옝ㅅ!!!
		sb.append("select topcategory_id from");
		sb.append("  topcategory where category_name='"+v+"') order by subcategory_id asc"); //order by where절 맨 끝에
		
		System.out.println(sb.toString());
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			
			//rs에 담겨진 레코드 1개는 SubCategory 클래스의 인스턴스 1개로 받자!!
			while(rs.next()){
			SubCategory dto=new SubCategory();
				dto.setSubcategory_id(rs.getInt("subcategory_id"));
				dto.setCategory_name(rs.getString("category_name"));
				dto.setTopcategory_id(rs.getInt("topcategory_id"));
				
				subcategory.add(dto); //컬렉션에 담기!!
				ch_sub.add(dto.getCategory_name());
			}
			
			//DB에서 테이블은 자바의 거푸집 개념. emp테이블에 scott을 넣으면 이것은 인스턴스
			//서브카테고리의 정보를 2차원 배열에 담기 + 출력
			//테이블이 만들어지면 클래스를 염두에 두기.
			//->ex) subcategory 레코드를 subcategory클래스의 거푸집으로부터 태어난 인스턴스, 
			//클래스는 반드시 로직만 존재하는 것이 아니라 인스턴스만 존재하기도 함.
		 //[레코드 수][컬럼 수]
			
		} catch (SQLException e) {
		
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					System.out.println("rs 끊기 실패");
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println("pstmt 끊기 실패");
					e.printStackTrace();
				}
			}
		}
	}
	
	//상품 등록 메서드 
	//등록을 누르면 그리드, 테이블에 추가 
	public void regist(){
		//기존에 등록된 자식(추가된 컴포넌트)이 있다면 컴포넌트 지우기
		Component[] comp=p_grid.getComponents(); //grid가 보유한 자식들 조사
		System.out.println("포함한 자식은 "+comp.length); 
		//removeAll써도 되지만, 다 지워 버리므로 이것을 이용하는 것이 좋을듯?
		
		//내가 지금 선택한 서브 카테고리 초이스의 index를 구해서, 그 index로 ArrayList를 접근하여 객체를 반환받으면
		//정보를 유용하게 쓸 수 있다.
		int index=ch_sub.getSelectedIndex();
		SubCategory dto=subcategory.get(index);
	
		String book_name=t_name.getText();//책이름
		int price=Integer.parseInt(t_price.getText()); //어차피 안에 들어가면 String형으로 받아지지만 자료형을 정확히 명시하는 것이 좋다!
		String img=file.getName(); //파일명
		
		
		StringBuffer sb=new StringBuffer();
		
	
		sb.append("insert into book(book_id, subcategory_id, book_name, price, img)"); //img는 이름만 넣고 하드디스크에 따로 저장할 예정
		sb.append("values(seq_book.nextval,"+dto.getSubcategory_id()+",'"+book_name+"',"+price+",'"+img+"')");
		
		//화면에도 보여지도록
		
		
		System.out.println(sb.toString());
		
		PreparedStatement pstmt=null;
		try {
			pstmt=con.prepareStatement(sb.toString());
			
			//SQL문이 DML(insert, delete, update) 데이터 조작어일 경우
			int result=pstmt.executeUpdate(); 
			
			//위의 메서드는 숫자값을 반환하며, 이 숫자값은 이 쿼리에 의해 영향을 받는 레코드 수를 반환한다
			//insert의 경우 언제나 1이 반환된다
			if(result!=0){
				//System.out.println(book_name+"등록 성공");				
				copy();
				
				((TablePanel)p_table).init(); //조회 일으킴
				((TablePanel)p_table).table.updateUI();; //UI 갱신
				
			}else{
				System.out.println(book_name+"등록 실패");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

	public void itemStateChanged(ItemEvent e) {
		Object obj=e.getSource();
		if(obj==ch_top){
			Choice ch=(Choice)e.getSource();
			getSub(ch.getSelectedItem());
		}else if(obj==ch_table){
			//System.out.println("테이블 볼래?");
			p_table.setVisible(true);
			p_grid.setVisible(false);
		}else if(obj==ch_grid){
			//System.out.println("그리드 볼래?");
			p_table.setVisible(false);
			p_grid.setVisible(true);
		}
		
	}
	
	//그림파일 불러오기
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		
		if(result==JFileChooser.APPROVE_OPTION){
			//선택한 이미지를 canvas에 그릴것이다!
			file=chooser.getSelectedFile();
			img=kit.getImage(file.getAbsolutePath());
			can.repaint();
			
		}
	}
	
	/*이미지 복사하기
	유저가 선택한 이미지를, 개발자가 지정한 위치로 복사를 해놓자!
	*/
	public void copy(){
		FileInputStream fis=null;
		FileOutputStream fos=null;
		
		try {
			fis=new FileInputStream(file);
			
			String filename=file.getName();
			String dest="C:/java_workspace2/DBProject2/data/"+filename;
			fos=new FileOutputStream(dest);
			
			int data; //읽어들인 데이터가 들어가지 않음. 개수만 들어감. 
			byte[] b=new byte[1024]; //한번 읽을 때 1 KByte 씩 읽겠다 ,[용량]개발자가 정할 수 있음..
			while(true){
				data=fis.read(b); // read(byte[] b) ->일종의 버퍼 , 속도 빠름 , 배열에 담은 다음에 빨아들임->실제 데이터는 byte에 들어감
				fos.write(b); //write(byte[] b)
				if(data==-1)break;
			}
			JOptionPane.showMessageDialog(this, "등록 완료");
		} catch (FileNotFoundException e) {
			System.out.println("파일을 찾을 수 없습니다");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("파일을 읽을 수 없습니다");
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	
	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("나 누름?");
		regist();
	
		
	}
	
	public static void main(String[] args) {
		new BookMain();
	}



}
