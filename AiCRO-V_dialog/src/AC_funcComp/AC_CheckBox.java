package AC_funcComp;

import java.awt.Color;
import java.awt.Component;

import java.awt.Graphics;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import AC_Viewer.DefultURL;
import AC_Viewer.Environment;




public class AC_CheckBox  extends JCheckBox {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7434870903965518189L;
	
	public AC_CheckBox()
	{
		init();
	}
	
	
	public AC_CheckBox(boolean selection)
	{
		init();
		this.setFocusable(true);
		this.setSelected(selection);

		
	}
	
	private void init()
	{
		
		this.setBackground(new Color(0,0,0));
			

		this.setIcon(new CheckBoxIcon(20));
	}
	public void setParentsSize(Graphics ParentsG, AC_TxtPanel tmp,int iWidth, int iHight)
	{
		
	
		int iLocationY = (int) (tmp.getSize().getHeight());
		this.setLocation(iLocationY/6,iLocationY-(iLocationY/4));
		
		 float dFSzie = (float)(iWidth+iHight
				 )/5000.0F;
		 int iOffset = 4;
		 int iSize = (int)(dFSzie*(Environment.g_CheckBoxSize*iOffset));
		 
			this.setSize(iSize,iSize);
			
			this.setIcon(new CheckBoxIcon(iSize));

	}
	
}



class CheckBoxIcon implements Icon {
	
	private int m_iSize = 10;
	
	public CheckBoxIcon(int iSize)
	{
		m_iSize = iSize;
	}
			
			
	public void paintIcon(Component component, Graphics g, int x, int y) {
		AbstractButton abstractButton = (AbstractButton)component;
		ButtonModel buttonModel = abstractButton.getModel();

		/*Color color = buttonModel.isSelected() ?  Color.BLUE : Color.RED;
		g.setColor(color);*/
		
		URL icon = buttonModel.isSelected() ? 
				getClass().getClassLoader().getResource(DefultURL.ICON_Link) :
				getClass().getClassLoader().getResource(DefultURL.ICON_Unlie) ;
		ImageIcon tmp = new ImageIcon(icon);
		
		g.drawImage(tmp.getImage(), 0, 0,m_iSize-2, m_iSize-2, component);
		
		
	//	g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
		
	//	g.drawImage(tmp.getImage(), 0, 0,m_iSize-2, m_iSize-2, component);

		//g.drawRect(1, 1, m_iSize-2,m_iSize-2);
		//tmp.paintIcon(component, g, 1, 1);

	}
	public int getIconWidth() {
		return m_iSize;
	}
	public int getIconHeight() {
		return m_iSize;
	}
}

