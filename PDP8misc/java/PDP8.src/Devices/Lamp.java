/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import java.awt.Graphics2D;
/*   7:    */ import java.awt.RenderingHints;
/*   8:    */ import java.awt.event.FocusEvent;
/*   9:    */ import java.awt.event.FocusListener;
/*  10:    */ import javax.swing.JComponent;
/*  11:    */ 
/*  12:    */ public class Lamp
/*  13:    */   extends JComponent
/*  14:    */ {
/*  15:    */   private boolean state;
/*  16:    */   private Color lampColor;
/*  17:    */   private double width;
/*  18:    */   private double height;
/*  19:    */   private double size;
/*  20: 27 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  21:    */   
/*  22:    */   public Lamp()
/*  23:    */   {
/*  24: 33 */     setState(true);
/*  25:    */     
/*  26: 35 */     addFocusListener(new FocusListener()
/*  27:    */     {
/*  28:    */       public void focusGained(FocusEvent e)
/*  29:    */       {
/*  30: 37 */         Lamp.this.repaint();
/*  31:    */       }
/*  32:    */       
/*  33:    */       public void focusLost(FocusEvent e)
/*  34:    */       {
/*  35: 40 */         Lamp.this.repaint();
/*  36:    */       }
/*  37:    */     });
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setPreferredSize(Dimension dim)
/*  41:    */   {
/*  42: 46 */     this.width = ((int)dim.getWidth());
/*  43: 47 */     this.height = ((int)dim.getHeight());
/*  44: 48 */     super.setPreferredSize(dim);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setState(int state)
/*  48:    */   {
/*  49: 59 */     if (state > 0) {
/*  50: 60 */       setState(true);
/*  51:    */     } else {
/*  52: 63 */       setState(false);
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setState(boolean state)
/*  57:    */   {
/*  58: 67 */     if (this.state == state) {
/*  59: 68 */       return;
/*  60:    */     }
/*  61: 71 */     this.state = state;
/*  62: 72 */     if (state == true) {
/*  63: 73 */       this.lampColor = Color.WHITE;
/*  64:    */     } else {
/*  65: 75 */       this.lampColor = Color.gray;
/*  66:    */     }
/*  67: 77 */     repaint();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean getState()
/*  71:    */   {
/*  72: 81 */     return this.state;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void paint(Graphics g)
/*  76:    */   {
/*  77: 87 */     Graphics2D g2d = (Graphics2D)g;
/*  78: 88 */     g2d.addRenderingHints(AALIAS);
/*  79:    */     
/*  80: 90 */     this.width = getWidth();
/*  81: 91 */     this.height = getHeight();
/*  82: 92 */     if (this.width > this.height) {
/*  83: 93 */       this.size = (this.height * 0.5D);
/*  84:    */     } else {
/*  85: 95 */       this.size = (this.width * 0.5D);
/*  86:    */     }
/*  87: 97 */     g.setColor(Color.darkGray);
/*  88: 98 */     g.fillRect(0, 0, (int)this.width, (int)this.height);
/*  89: 99 */     g.setColor(this.lampColor);
/*  90:100 */     g.fillOval((int)(this.width / 2.0D - this.size / 2.0D), (int)(this.height / 2.0D - this.size / 2.0D), (int)this.size, (int)this.size);
/*  91:    */   }
/*  92:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.Lamp
 * JD-Core Version:    0.7.0.1
 */