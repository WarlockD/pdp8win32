/*  1:   */ package Panel;
/*  2:   */ 
/*  3:   */ import java.awt.Color;
/*  4:   */ import java.awt.Graphics;
/*  5:   */ import java.awt.Graphics2D;
/*  6:   */ import java.awt.RenderingHints;
/*  7:   */ import java.awt.event.FocusEvent;
/*  8:   */ import java.awt.event.FocusListener;
/*  9:   */ import javax.swing.JComponent;
/* 10:   */ 
/* 11:   */ public class Lamp
/* 12:   */   extends JComponent
/* 13:   */ {
/* 14:   */   private boolean state;
/* 15:   */   private Color lampColor;
/* 16:   */   private int width;
/* 17:   */   private int height;
/* 18:   */   private int size;
/* 19:27 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 20:   */   
/* 21:   */   public Lamp()
/* 22:   */   {
/* 23:33 */     setState(true);
/* 24:   */     
/* 25:35 */     addFocusListener(new FocusListener()
/* 26:   */     {
/* 27:   */       public void focusGained(FocusEvent e)
/* 28:   */       {
/* 29:37 */         Lamp.this.repaint();
/* 30:   */       }
/* 31:   */       
/* 32:   */       public void focusLost(FocusEvent e)
/* 33:   */       {
/* 34:40 */         Lamp.this.repaint();
/* 35:   */       }
/* 36:   */     });
/* 37:   */   }
/* 38:   */   
/* 39:   */   public void setState(int state)
/* 40:   */   {
/* 41:46 */     if (state > 0) {
/* 42:47 */       setState(true);
/* 43:   */     } else {
/* 44:50 */       setState(false);
/* 45:   */     }
/* 46:   */   }
/* 47:   */   
/* 48:   */   public void setState(boolean state)
/* 49:   */   {
/* 50:54 */     if (this.state == state) {
/* 51:55 */       return;
/* 52:   */     }
/* 53:58 */     this.state = state;
/* 54:59 */     if (state == true) {
/* 55:60 */       this.lampColor = Color.YELLOW;
/* 56:   */     } else {
/* 57:62 */       this.lampColor = Color.BLACK;
/* 58:   */     }
/* 59:64 */     repaint();
/* 60:   */   }
/* 61:   */   
/* 62:   */   public boolean getState()
/* 63:   */   {
/* 64:68 */     return this.state;
/* 65:   */   }
/* 66:   */   
/* 67:   */   public void paint(Graphics g)
/* 68:   */   {
/* 69:74 */     Graphics2D g2d = (Graphics2D)g;
/* 70:75 */     g2d.addRenderingHints(AALIAS);
/* 71:   */     
/* 72:77 */     this.width = getWidth();
/* 73:78 */     this.height = getHeight();
/* 74:79 */     if (this.width > this.height) {
/* 75:80 */       this.size = ((int)(this.height * 0.7D));
/* 76:   */     } else {
/* 77:82 */       this.size = ((int)(this.width * 0.7D));
/* 78:   */     }
/* 79:84 */     g.setColor(Color.BLACK);
/* 80:85 */     g.fillRect(0, 0, this.width, this.height);
/* 81:86 */     g.setColor(this.lampColor);
/* 82:87 */     g.fillOval((int)(this.width / 2.0F - this.size / 2.0F), (int)(this.height / 2.0F - this.size / 2.0F), this.size, this.size);
/* 83:   */   }
/* 84:   */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.Lamp
 * JD-Core Version:    0.7.0.1
 */