package AC_funcComp;

import java.awt.Font;

import javax.swing.JLabel;


public class AC_AutoLabel extends JLabel  {
	 
    public AC_AutoLabel(String sinput) {
  
      
        this.setText(sinput);
       
     
    }
    
   
    
 
   public  void resize() {
	   
	
    	Font labelFont = this.getFont();
    	String labelText = this.getText();
    	
    	System.out.println("FontSize"+labelFont);
     	System.out.println("labelText"+labelText);
     	
   
    	
    	
    	if(labelText==null )
    	{
    		return;
    	}   	
    	if(labelFont.getSize()==0)
    	{
    		System.out.println("FontSize"+labelFont);
    		this.setFont(new Font(labelFont.getName(), Font.PLAIN,  this.getHeight()));
    		return;
    	}
    	
    	
    	int stringWidth = this.getFontMetrics(labelFont).stringWidth(labelText);
    	int componentWidth = this.getWidth();

    	// Find out how much the font can grow in width.
    	double widthRatio = (double)componentWidth / (double)stringWidth;

    	int newFontSize = (int)(labelFont.getSize() * widthRatio);
    	int componentHeight = this.getHeight();
    	
    	System.out.println("newFontSize"+newFontSize);
    	System.out.println("componentHeight"+componentHeight);


    	// Pick a new font size so it will not be larger than the height of label.
    	int fontSizeToUse = Math.min(newFontSize, componentHeight);
     	System.out.println("fontSizeToUse"+fontSizeToUse);
    	// Set the label's font size to the newly determined size.
    	this.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
    }





}


