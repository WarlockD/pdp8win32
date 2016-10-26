/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.GridBagConstraints;
/*   5:    */ import java.awt.GridBagLayout;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ 
/*   8:    */ public class MaLamps
/*   9:    */   extends JPanel
/*  10:    */ {
/*  11:    */   private Lamp maLamp0;
/*  12:    */   private Lamp maLamp1;
/*  13:    */   private Lamp maLamp10;
/*  14:    */   private Lamp maLamp11;
/*  15:    */   private Lamp maLamp2;
/*  16:    */   private Lamp maLamp3;
/*  17:    */   private Lamp maLamp4;
/*  18:    */   private Lamp maLamp5;
/*  19:    */   private Lamp maLamp6;
/*  20:    */   private Lamp maLamp7;
/*  21:    */   private Lamp maLamp8;
/*  22:    */   private Lamp maLamp9;
/*  23:    */   
/*  24:    */   public MaLamps()
/*  25:    */   {
/*  26: 18 */     initComponents();
/*  27:    */   }
/*  28:    */   
/*  29:    */   private void initComponents()
/*  30:    */   {
/*  31: 27 */     this.maLamp0 = new Lamp();
/*  32: 28 */     this.maLamp1 = new Lamp();
/*  33: 29 */     this.maLamp2 = new Lamp();
/*  34: 30 */     this.maLamp3 = new Lamp();
/*  35: 31 */     this.maLamp4 = new Lamp();
/*  36: 32 */     this.maLamp5 = new Lamp();
/*  37: 33 */     this.maLamp6 = new Lamp();
/*  38: 34 */     this.maLamp7 = new Lamp();
/*  39: 35 */     this.maLamp8 = new Lamp();
/*  40: 36 */     this.maLamp9 = new Lamp();
/*  41: 37 */     this.maLamp10 = new Lamp();
/*  42: 38 */     this.maLamp11 = new Lamp();
/*  43:    */     
/*  44: 40 */     setLayout(new GridBagLayout());
/*  45:    */     
/*  46: 42 */     setMaximumSize(new Dimension(312, 10));
/*  47: 43 */     setMinimumSize(new Dimension(156, 5));
/*  48: 44 */     setPreferredSize(new Dimension(312, 10));
/*  49: 45 */     this.maLamp0.setMaximumSize(new Dimension(26, 10));
/*  50: 46 */     this.maLamp0.setMinimumSize(new Dimension(13, 5));
/*  51: 47 */     this.maLamp0.setPreferredSize(new Dimension(26, 10));
/*  52: 48 */     add(this.maLamp0, new GridBagConstraints());
/*  53:    */     
/*  54: 50 */     this.maLamp1.setMaximumSize(new Dimension(26, 10));
/*  55: 51 */     this.maLamp1.setMinimumSize(new Dimension(13, 5));
/*  56: 52 */     this.maLamp1.setPreferredSize(new Dimension(26, 10));
/*  57: 53 */     add(this.maLamp1, new GridBagConstraints());
/*  58:    */     
/*  59: 55 */     this.maLamp2.setMaximumSize(new Dimension(26, 10));
/*  60: 56 */     this.maLamp2.setMinimumSize(new Dimension(13, 5));
/*  61: 57 */     this.maLamp2.setPreferredSize(new Dimension(26, 10));
/*  62: 58 */     add(this.maLamp2, new GridBagConstraints());
/*  63:    */     
/*  64: 60 */     this.maLamp3.setMaximumSize(new Dimension(26, 10));
/*  65: 61 */     this.maLamp3.setMinimumSize(new Dimension(13, 5));
/*  66: 62 */     this.maLamp3.setPreferredSize(new Dimension(26, 10));
/*  67: 63 */     add(this.maLamp3, new GridBagConstraints());
/*  68:    */     
/*  69: 65 */     this.maLamp4.setMaximumSize(new Dimension(26, 10));
/*  70: 66 */     this.maLamp4.setMinimumSize(new Dimension(13, 5));
/*  71: 67 */     this.maLamp4.setPreferredSize(new Dimension(26, 10));
/*  72: 68 */     add(this.maLamp4, new GridBagConstraints());
/*  73:    */     
/*  74: 70 */     this.maLamp5.setMaximumSize(new Dimension(26, 10));
/*  75: 71 */     this.maLamp5.setMinimumSize(new Dimension(13, 5));
/*  76: 72 */     this.maLamp5.setPreferredSize(new Dimension(26, 10));
/*  77: 73 */     add(this.maLamp5, new GridBagConstraints());
/*  78:    */     
/*  79: 75 */     this.maLamp6.setMaximumSize(new Dimension(26, 10));
/*  80: 76 */     this.maLamp6.setMinimumSize(new Dimension(13, 5));
/*  81: 77 */     this.maLamp6.setPreferredSize(new Dimension(26, 10));
/*  82: 78 */     add(this.maLamp6, new GridBagConstraints());
/*  83:    */     
/*  84: 80 */     this.maLamp7.setMaximumSize(new Dimension(26, 10));
/*  85: 81 */     this.maLamp7.setMinimumSize(new Dimension(13, 5));
/*  86: 82 */     this.maLamp7.setPreferredSize(new Dimension(26, 10));
/*  87: 83 */     add(this.maLamp7, new GridBagConstraints());
/*  88:    */     
/*  89: 85 */     this.maLamp8.setMaximumSize(new Dimension(26, 10));
/*  90: 86 */     this.maLamp8.setMinimumSize(new Dimension(13, 5));
/*  91: 87 */     this.maLamp8.setPreferredSize(new Dimension(26, 10));
/*  92: 88 */     add(this.maLamp8, new GridBagConstraints());
/*  93:    */     
/*  94: 90 */     this.maLamp9.setMaximumSize(new Dimension(26, 10));
/*  95: 91 */     this.maLamp9.setMinimumSize(new Dimension(13, 5));
/*  96: 92 */     this.maLamp9.setPreferredSize(new Dimension(26, 10));
/*  97: 93 */     add(this.maLamp9, new GridBagConstraints());
/*  98:    */     
/*  99: 95 */     this.maLamp10.setMaximumSize(new Dimension(26, 10));
/* 100: 96 */     this.maLamp10.setMinimumSize(new Dimension(13, 5));
/* 101: 97 */     this.maLamp10.setPreferredSize(new Dimension(26, 10));
/* 102: 98 */     add(this.maLamp10, new GridBagConstraints());
/* 103:    */     
/* 104:100 */     this.maLamp11.setMaximumSize(new Dimension(26, 10));
/* 105:101 */     this.maLamp11.setMinimumSize(new Dimension(13, 5));
/* 106:102 */     this.maLamp11.setPreferredSize(new Dimension(26, 10));
/* 107:103 */     add(this.maLamp11, new GridBagConstraints());
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setState(int state)
/* 111:    */   {
/* 112:108 */     this.state = state;
/* 113:109 */     this.maLamp0.setState(state & 0x800);
/* 114:110 */     this.maLamp1.setState(state & 0x400);
/* 115:111 */     this.maLamp2.setState(state & 0x200);
/* 116:112 */     this.maLamp3.setState(state & 0x100);
/* 117:113 */     this.maLamp4.setState(state & 0x80);
/* 118:114 */     this.maLamp5.setState(state & 0x40);
/* 119:115 */     this.maLamp6.setState(state & 0x20);
/* 120:116 */     this.maLamp7.setState(state & 0x10);
/* 121:117 */     this.maLamp8.setState(state & 0x8);
/* 122:118 */     this.maLamp9.setState(state & 0x4);
/* 123:119 */     this.maLamp10.setState(state & 0x2);
/* 124:120 */     this.maLamp11.setState(state & 0x1);
/* 125:    */   }
/* 126:    */   
/* 127:139 */   private int state = -1;
/* 128:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.MaLamps
 * JD-Core Version:    0.7.0.1
 */