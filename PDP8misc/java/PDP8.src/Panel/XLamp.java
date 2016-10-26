/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.awt.FontMetrics;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Graphics2D;
/*   8:    */ import java.awt.RenderingHints;
/*   9:    */ import java.awt.event.FocusEvent;
/*  10:    */ import java.awt.event.FocusListener;
/*  11:    */ import java.io.Serializable;
/*  12:    */ import javax.swing.JComponent;
/*  13:    */ 
/*  14:    */ public class XLamp
/*  15:    */   extends JComponent
/*  16:    */   implements Serializable
/*  17:    */ {
/*  18:    */   private boolean state;
/*  19:    */   private Color lampColor;
/*  20:    */   private Color backColor;
/*  21: 22 */   private String ourText = " ";
/*  22:    */   private Font ourFont;
/*  23:    */   private int width;
/*  24:    */   private int height;
/*  25:    */   private int size;
/*  26: 27 */   private int edge = 0;
/*  27: 29 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  28:    */   
/*  29:    */   public XLamp()
/*  30:    */   {
/*  31: 34 */     setState(true);
/*  32:    */     
/*  33: 36 */     addFocusListener(new FocusListener()
/*  34:    */     {
/*  35:    */       public void focusGained(FocusEvent e)
/*  36:    */       {
/*  37: 39 */         XLamp.this.repaint();
/*  38:    */       }
/*  39:    */       
/*  40:    */       public void focusLost(FocusEvent e)
/*  41:    */       {
/*  42: 43 */         XLamp.this.repaint();
/*  43:    */       }
/*  44:    */     });
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setState(int state)
/*  48:    */   {
/*  49: 49 */     if (state > 0) {
/*  50: 50 */       setState(true);
/*  51:    */     } else {
/*  52: 52 */       setState(false);
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setBackground(Color backc)
/*  57:    */   {
/*  58: 58 */     this.backColor = backc;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Color getBackground()
/*  62:    */   {
/*  63: 63 */     return this.backColor;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setText(String ourtext)
/*  67:    */   {
/*  68: 67 */     if (this.ourText.equals(ourtext)) {
/*  69: 68 */       return;
/*  70:    */     }
/*  71: 70 */     this.ourText = ourtext;
/*  72: 71 */     repaint();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String getText()
/*  76:    */   {
/*  77: 75 */     return this.ourText;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setEdge(int edge)
/*  81:    */   {
/*  82: 79 */     this.edge = edge;
/*  83: 80 */     repaint();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int getEdge()
/*  87:    */   {
/*  88: 84 */     return this.edge;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setFont(Font ourfont)
/*  92:    */   {
/*  93: 89 */     this.ourFont = ourfont;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Font getFont()
/*  97:    */   {
/*  98: 95 */     return this.ourFont;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setState(boolean state)
/* 102:    */   {
/* 103: 99 */     if (this.state == state) {
/* 104:100 */       return;
/* 105:    */     }
/* 106:103 */     this.state = state;
/* 107:104 */     if (state == true) {
/* 108:105 */       this.lampColor = Color.ORANGE;
/* 109:    */     } else {
/* 110:107 */       this.lampColor = this.backColor;
/* 111:    */     }
/* 112:109 */     repaint();
/* 113:    */   }
/* 114:    */   
/* 115:    */   public boolean getState()
/* 116:    */   {
/* 117:113 */     return this.state;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void paint(Graphics g)
/* 121:    */   {
/* 122:118 */     Graphics2D g2d = (Graphics2D)g;
/* 123:119 */     g2d.addRenderingHints(AALIAS);
/* 124:    */     
/* 125:121 */     this.width = getWidth();
/* 126:122 */     this.height = getHeight();
/* 127:123 */     if (this.width > this.height) {
/* 128:124 */       this.size = ((int)(this.height * 0.7D));
/* 129:    */     } else {
/* 130:126 */       this.size = ((int)(this.width * 0.7D));
/* 131:    */     }
/* 132:128 */     FontMetrics fm = g.getFontMetrics(this.ourFont);
/* 133:129 */     int sw = fm.stringWidth(this.ourText);
/* 134:130 */     int sh = fm.getAscent();
/* 135:131 */     g.setColor(this.backColor);
/* 136:132 */     g.fillRect(0, 0, this.width, this.height);
/* 137:133 */     g.setColor(this.lampColor);
/* 138:134 */     g.fillOval((int)(this.width / 2.0F - this.size / 2.0F), (int)(this.height / 2.0F - this.size / 2.0F), this.size, this.size);
/* 139:135 */     g.setColor(Color.WHITE);
/* 140:136 */     g.setFont(this.ourFont);
/* 141:137 */     g.drawString(this.ourText, (this.width - sw) / 2, (this.height + sh) / 2 - 1);
/* 142:138 */     if ((this.edge == 1) || (this.edge == 3))
/* 143:    */     {
/* 144:139 */       g.drawLine(0, 0, 0, this.height - 1);
/* 145:140 */       g.drawLine(this.width - 1, 0, this.width - 1, this.height - 1);
/* 146:    */     }
/* 147:142 */     if ((this.edge == 2) || (this.edge == 3)) {
/* 148:144 */       g.drawLine(0, this.height - 1, this.width - 1, this.height - 1);
/* 149:    */     }
/* 150:    */   }
/* 151:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.XLamp
 * JD-Core Version:    0.7.0.1
 */