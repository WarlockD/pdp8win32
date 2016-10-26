/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import java.awt.BasicStroke;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Graphics2D;
/*   8:    */ import java.awt.RenderingHints;
/*   9:    */ import java.awt.Stroke;
/*  10:    */ import java.awt.geom.AffineTransform;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import javax.swing.JComponent;
/*  13:    */ 
/*  14:    */ public class DiskUnit
/*  15:    */   extends JComponent
/*  16:    */ {
/*  17:    */   private Disk4043 d43;
/*  18:    */   private Heads doit;
/*  19: 23 */   private int unit = 0;
/*  20: 24 */   private boolean first = false;
/*  21:    */   private int width;
/*  22:    */   private int height;
/*  23:    */   private int pos;
/*  24:    */   private Color back;
/*  25:    */   private Color dark;
/*  26:    */   private int local_track;
/*  27:    */   private Thread update;
/*  28: 33 */   private static final Dimension MIN_SIZE = new Dimension(150, 20);
/*  29: 34 */   private static final Dimension PREF_SIZE = new Dimension(300, 40);
/*  30: 37 */   private static final RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  31: 40 */   Stroke defaultStroke = new BasicStroke(1.0F);
/*  32: 41 */   Stroke diskStroke = new BasicStroke(2.0F);
/*  33: 42 */   Stroke headStroke = new BasicStroke(4.0F);
/*  34:    */   
/*  35:    */   public DiskUnit()
/*  36:    */   {
/*  37: 47 */     setPreferredSize(PREF_SIZE);
/*  38: 48 */     setMinimumSize(MIN_SIZE);
/*  39: 49 */     setBackground(new Color(100, 100, 100));
/*  40: 50 */     this.doit = new Heads();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void startUnit(Disk4043 disk, String name)
/*  44:    */   {
/*  45: 54 */     this.d43 = disk;
/*  46: 55 */     this.first = true;
/*  47: 56 */     this.update = new Thread(this.doit, name);
/*  48: 57 */     this.update.start();
/*  49:    */   }
/*  50:    */   
/*  51:    */   public int getUnit()
/*  52:    */   {
/*  53: 61 */     return this.unit;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setUnit(int unit)
/*  57:    */   {
/*  58: 65 */     this.unit = unit;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Color getBackground()
/*  62:    */   {
/*  63: 70 */     return this.back;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setBackground(Color back)
/*  67:    */   {
/*  68: 75 */     this.back = back;
/*  69: 76 */     this.dark = back.darker().darker();
/*  70: 77 */     repaint();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void paint(Graphics g1d)
/*  74:    */   {
/*  75: 84 */     this.width = getWidth();
/*  76: 85 */     this.height = getHeight();
/*  77:    */     
/*  78: 87 */     Graphics2D g = (Graphics2D)g1d;
/*  79: 88 */     g.setStroke(this.defaultStroke);
/*  80: 89 */     g.setColor(this.back);
/*  81: 90 */     g.fillRect(0, 0, this.width, this.height);
/*  82: 91 */     g.setColor(Color.black);
/*  83: 92 */     g.setStroke(this.diskStroke);
/*  84: 93 */     g.drawLine(0, 0, this.width, 0);
/*  85: 94 */     g.drawLine(0, this.height, this.width, this.height);
/*  86: 95 */     if (this.first == true) {
/*  87: 96 */       if (((this.d43.si3040.sel[this.unit][0] == 1 ? 1 : 0) | (this.d43.si3040.sel[this.unit][1] == 1 ? 1 : 0)) != 0)
/*  88:    */       {
/*  89: 97 */         g.setStroke(this.diskStroke);
/*  90: 98 */         g.setColor(new Color(193, 132, 28));
/*  91: 99 */         g.fillRect(this.width * 7 / 20, this.height * 1 / 10, this.width * 1 / 10, this.height * 8 / 10);
/*  92:100 */         g.setColor(new Color(193, 73, 28));
/*  93:101 */         if (this.d43.si3040.sel[this.unit][0] == 1) {
/*  94:102 */           g.drawLine(this.width / 10, this.height * 3 / 10, this.width * 7 / 10, this.height * 3 / 10);
/*  95:    */         }
/*  96:103 */         if (this.d43.si3040.sel[this.unit][1] == 1) {
/*  97:104 */           g.drawLine(this.width / 10, this.height * 7 / 10, this.width * 7 / 10, this.height * 7 / 10);
/*  98:    */         }
/*  99:105 */         g.setColor(Color.black);
/* 100:106 */         g.setStroke(this.diskStroke);
/* 101:107 */         g.drawLine(this.width * 15 / 20, this.height * 2 / 20, this.width, this.height * 2 / 20);
/* 102:108 */         g.drawLine(this.width * 15 / 20, this.height * 18 / 20, this.width, this.height * 18 / 20);
/* 103:109 */         this.pos = (this.local_track * this.width * 3 / 8160 + this.width / 40);
/* 104:110 */         AffineTransform a = AffineTransform.getTranslateInstance(-this.pos, 0.0D);
/* 105:111 */         g.transform(a);
/* 106:112 */         g.setColor(new Color(153, 53, 0));
/* 107:113 */         g.setStroke(this.diskStroke);
/* 108:114 */         g.fillRect(this.width * 18 / 20, this.height * 3 / 20, this.width * 1 / 10, this.height * 14 / 20);
/* 109:115 */         g.setColor(Color.white);
/* 110:116 */         g.drawLine(this.width * 13 / 20, this.height * 2 / 10, this.width * 18 / 20, this.height * 2 / 10);
/* 111:117 */         g.drawLine(this.width * 13 / 20, this.height * 4 / 10, this.width * 18 / 20, this.height * 4 / 10);
/* 112:118 */         g.drawLine(this.width * 13 / 20, this.height * 6 / 10, this.width * 18 / 20, this.height * 6 / 10);
/* 113:119 */         g.drawLine(this.width * 13 / 20, this.height * 8 / 10, this.width * 18 / 20, this.height * 8 / 10);
/* 114:120 */         g.setStroke(this.headStroke);
/* 115:121 */         g.drawLine(this.width * 13 / 20, this.height * 2 / 10, this.width * 14 / 20, this.height * 2 / 10);
/* 116:122 */         g.drawLine(this.width * 13 / 20, this.height * 4 / 10, this.width * 14 / 20, this.height * 4 / 10);
/* 117:123 */         g.drawLine(this.width * 13 / 20, this.height * 6 / 10, this.width * 14 / 20, this.height * 6 / 10);
/* 118:124 */         g.drawLine(this.width * 13 / 20, this.height * 8 / 10, this.width * 14 / 20, this.height * 8 / 10);
/* 119:    */       }
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   public class Heads
/* 124:    */     implements Runnable
/* 125:    */   {
/* 126:130 */     int sleep = 20;
/* 127:    */     
/* 128:    */     public void run()
/* 129:    */     {
/* 130:    */       for (;;)
/* 131:    */       {
/* 132:    */         try
/* 133:    */         {
/* 134:134 */           if (DiskUnit.this.local_track != DiskUnit.this.d43.si3040.track[DiskUnit.this.unit])
/* 135:    */           {
/* 136:135 */             DiskUnit.this.local_track = DiskUnit.this.d43.si3040.track[DiskUnit.this.unit];
/* 137:136 */             DiskUnit.this.repaint();
/* 138:137 */             this.sleep = 20;
/* 139:    */           }
/* 140:139 */           Thread.sleep(this.sleep);
/* 141:140 */           this.sleep += 1;
/* 142:    */           
/* 143:142 */           continue;System.out.println("Track error" + e);
/* 144:    */         }
/* 145:    */         catch (InterruptedException e) {}
/* 146:    */       }
/* 147:    */     }
/* 148:    */     
/* 149:    */     public Heads() {}
/* 150:    */   }
/* 151:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.DiskUnit
 * JD-Core Version:    0.7.0.1
 */