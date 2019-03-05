package AC_funcComp;




import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;



public class AC_IPPopUpMeum extends JPopupMenu implements ActionListener{
	
	

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private JMenuItem saveROI= new JMenuItem("ROI 저장");
	private JMenuItem loadROI= new JMenuItem("ROI 불러오기");
	private JMenuItem removeROI= new JMenuItem("ROI 전체삭제");
	
	public AC_IPPopUpMeum()
	{
		new JPopupMenu();
		this.add(saveROI);
		this.add(loadROI);
		this.add(removeROI);
		saveROI.addActionListener(this);
		loadROI.addActionListener(this);
		removeROI.addActionListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		/*if(e.getSource() == saveROI)
		{
			String sSavePath = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showSaveDialog(this);
			
	        if( returnVal == JFileChooser.APPROVE_OPTION)
	        {
	            //취소 버튼을 누르면
	            File file = fileChooser.getSelectedFile();
	            sSavePath = file.getAbsolutePath();
	            
	            try {
					System.out.println("sSavePath" + sSavePath);
					
				//	m_sereis.saveRoiFile(sSavePath);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         //   address.setText(file.toString()+"에 저장합니다");
	        }
	        else
	        {
	            //취소 버튼을 누르면
	           // address.setText("파일을 저장하지 못했습니다");
	        }
			
		}
		
		/*if(e.getSource() == loadROI)
		{
			String sSavePath = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fileChooser.showSaveDialog(this);
			
	        if( returnVal == JFileChooser.APPROVE_OPTION)
	        {
	            //취소 버튼을 누르면
	            File file = fileChooser.getSelectedFile();
	            sSavePath = file.getAbsolutePath();
	            
	            try {
					System.out.println("sSavePath" + sSavePath);
					
				//	m_sereis.readRoiFile(sSavePath);
					this.repaint();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         //   address.setText(file.toString()+"에 저장합니다");
	        }
	        else
	        {
	            //취소 버튼을 누르면
	           // address.setText("파일을 저장하지 못했습니다");
	        }
			
		}
		if(e.getSource() == removeROI)
		{
		//	m_sereis.removeROI(-1);
		
		}*/
		
	}
	
	

}
