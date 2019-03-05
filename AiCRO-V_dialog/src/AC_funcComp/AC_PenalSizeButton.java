package AC_funcComp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JPopupMenu;


import AC_Viewer.AC_ImageIconHeadler;
import AC_Viewer.DefultURL;
import AC_Viewer.ViewerMain;


public class AC_PenalSizeButton extends JButton implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Color defultColor = Color.gray;
	JPopupMenu m_Popup;

	private int m_iRow = 1;
	private int m_iColumn = 1;
	private int m_nTotal = 1;
	ViewerMain m_mainFrame;

	private final int MAX_PANEL_ROW = 4;
	private final int MAX_PANEL_COLUNM = 5;

	public AC_PenalSizeButton(ViewerMain input)
	{
		m_mainFrame = input;
		init();
	}
	
	public int getRows()
	{
		return m_iRow;
	}
	public int getColumns()
	{
		return m_iColumn;
	}
	public int getTotal()
	{
		return m_nTotal;
	}

	private void init()
	{
		this.setBackground(defultColor);
		this.setBorderPainted(false);
		//URL icon = getClass().getClassLoader().getResource(DefultURL.ICON_SELECTION);
		URL icon = getClass().getClassLoader().getResource(DefultURL.ICON_SELECTION);
 		this.setIcon(AC_ImageIconHeadler.reSizeImageICON(icon));
		
		this.addActionListener(this);
		

	
		
		
		
		buildPopUp();
	}


	private void buildPopUp() {

		m_Popup = new JPopupMenu();
		m_Popup.setLayout(new GridLayout(MAX_PANEL_ROW, MAX_PANEL_COLUNM));
		m_Popup.setPopupSize(new Dimension(120,120));
		
		
		
		m_Popup.setToolTipText("sdfsdf");;




		JButton[] btn_Table = new JButton[MAX_PANEL_ROW*MAX_PANEL_COLUNM];

		for(int idx =0; idx<MAX_PANEL_ROW*MAX_PANEL_COLUNM;idx++  )
		{
			btn_Table[idx] = new JButton();
			btn_Table[idx].setName(Integer.toString(idx));
			btn_Table[idx].setSize(20, 20);
			btn_Table[idx].setBackground(new Color(255,255,255));
			/*String name = "btn_Table"+idx;
			btn_Table[idx].setName(name);*/
		


			


			btn_Table[idx].addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
				//	System.out.println("reslesd");

					m_Popup.setVisible(false);
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent e) {




					//System.out.println("pressd");
					// TODO Auto-generated method stub


					int iSecltionNum = Integer.parseInt(e.getComponent().getName());
				//	System.out.println("iSecltionNum : " +iSecltionNum);
					int iRow = (iSecltionNum/MAX_PANEL_COLUNM)+1;
					int iColum = (iSecltionNum%MAX_PANEL_COLUNM)+1;
					int iTotalNUM = iRow*iColum;

					m_iRow = iRow;
					m_iColumn =  iColum;
					m_nTotal = iTotalNUM;

					m_mainFrame.updateImagePanels(iTotalNUM, iRow,iColum);




					//	



				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

					//System.out.println("Eixted");
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub

					int iSecltionNum = Integer.parseInt(e.getComponent().getName());
				//	System.out.println("iSecltionNum : " +iSecltionNum);
					int iRow = iSecltionNum/MAX_PANEL_COLUNM;
					int iColum = iSecltionNum%MAX_PANEL_COLUNM;
				//	System.out.println("iRow : " +iRow);
					//System.out.println("iColum : "+iColum);

					
					for(int ix = 0; ix<MAX_PANEL_ROW*MAX_PANEL_COLUNM;ix++)
					{
						JButton jtmp = (JButton)m_Popup.getComponent(ix);
						jtmp.setBackground(new Color(255,255,255));


					}
					for(int ix = 0; ix<=iRow;ix++)
					{
						for(int iy = 0; iy<=iColum;iy++)
						{


							JButton jtmp = (JButton)m_Popup.getComponent((ix*MAX_PANEL_COLUNM)+iy);

							jtmp.setBackground(new Color(0,0,0));
						}

					}



					//System.out.println("Enter");

				}

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub

				}
			});
			m_Popup.add(btn_Table[idx]);
		}


		//


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		//System.out.println("adfadf");;

		/*if(this.isSelected())
		{
			this.setSelected(false);
		}
		else
		{*/
			//this.setSelected(true);

			for(int ix = 0; ix<MAX_PANEL_ROW*MAX_PANEL_COLUNM;ix++)
			{
				JButton jtmp = (JButton)m_Popup.getComponent(ix);
				jtmp.setBackground(new Color(255,255,255));


			}

			m_Popup.show(this, this.getX(), 
					m_Popup.getY()+this.getSize().height/2);
		//}

	}




}
