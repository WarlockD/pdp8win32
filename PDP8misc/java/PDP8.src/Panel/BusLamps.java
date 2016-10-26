/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.GridBagConstraints;
/*   5:    */ import java.awt.GridBagLayout;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ 
/*   8:    */ public class BusLamps
/*   9:    */   extends JPanel
/*  10:    */ {
/*  11:    */   private Lamp busLamp0;
/*  12:    */   private Lamp busLamp1;
/*  13:    */   private Lamp busLamp10;
/*  14:    */   private Lamp busLamp11;
/*  15:    */   private Lamp busLamp2;
/*  16:    */   private Lamp busLamp3;
/*  17:    */   private Lamp busLamp4;
/*  18:    */   private Lamp busLamp5;
/*  19:    */   private Lamp busLamp6;
/*  20:    */   private Lamp busLamp7;
/*  21:    */   private Lamp busLamp8;
/*  22:    */   private Lamp busLamp9;
/*  23:    */   
/*  24:    */   public BusLamps()
/*  25:    */   {
/*  26: 18 */     initComponents();
/*  27:    */   }
/*  28:    */   
/*  29:    */   private void initComponents()
/*  30:    */   {
/*  31: 27 */     this.busLamp0 = new Lamp();
/*  32: 28 */     this.busLamp1 = new Lamp();
/*  33: 29 */     this.busLamp2 = new Lamp();
/*  34: 30 */     this.busLamp3 = new Lamp();
/*  35: 31 */     this.busLamp4 = new Lamp();
/*  36: 32 */     this.busLamp5 = new Lamp();
/*  37: 33 */     this.busLamp6 = new Lamp();
/*  38: 34 */     this.busLamp7 = new Lamp();
/*  39: 35 */     this.busLamp8 = new Lamp();
/*  40: 36 */     this.busLamp9 = new Lamp();
/*  41: 37 */     this.busLamp10 = new Lamp();
/*  42: 38 */     this.busLamp11 = new Lamp();
/*  43:    */     
/*  44: 40 */     setLayout(new GridBagLayout());
/*  45:    */     
/*  46: 42 */     setMaximumSize(new Dimension(312, 10));
/*  47: 43 */     setMinimumSize(new Dimension(156, 5));
/*  48: 44 */     setPreferredSize(new Dimension(312, 10));
/*  49: 45 */     this.busLamp0.setMaximumSize(new Dimension(26, 10));
/*  50: 46 */     this.busLamp0.setMinimumSize(new Dimension(13, 5));
/*  51: 47 */     this.busLamp0.setPreferredSize(new Dimension(26, 10));
/*  52: 48 */     add(this.busLamp0, new GridBagConstraints());
/*  53:    */     
/*  54: 50 */     this.busLamp1.setMaximumSize(new Dimension(26, 10));
/*  55: 51 */     this.busLamp1.setMinimumSize(new Dimension(13, 5));
/*  56: 52 */     this.busLamp1.setPreferredSize(new Dimension(26, 10));
/*  57: 53 */     add(this.busLamp1, new GridBagConstraints());
/*  58:    */     
/*  59: 55 */     this.busLamp2.setMaximumSize(new Dimension(26, 10));
/*  60: 56 */     this.busLamp2.setMinimumSize(new Dimension(13, 5));
/*  61: 57 */     this.busLamp2.setPreferredSize(new Dimension(26, 10));
/*  62: 58 */     add(this.busLamp2, new GridBagConstraints());
/*  63:    */     
/*  64: 60 */     this.busLamp3.setMaximumSize(new Dimension(26, 10));
/*  65: 61 */     this.busLamp3.setMinimumSize(new Dimension(13, 5));
/*  66: 62 */     this.busLamp3.setPreferredSize(new Dimension(26, 10));
/*  67: 63 */     add(this.busLamp3, new GridBagConstraints());
/*  68:    */     
/*  69: 65 */     this.busLamp4.setMaximumSize(new Dimension(26, 10));
/*  70: 66 */     this.busLamp4.setMinimumSize(new Dimension(13, 5));
/*  71: 67 */     this.busLamp4.setPreferredSize(new Dimension(26, 10));
/*  72: 68 */     add(this.busLamp4, new GridBagConstraints());
/*  73:    */     
/*  74: 70 */     this.busLamp5.setMaximumSize(new Dimension(26, 10));
/*  75: 71 */     this.busLamp5.setMinimumSize(new Dimension(13, 5));
/*  76: 72 */     this.busLamp5.setPreferredSize(new Dimension(26, 10));
/*  77: 73 */     add(this.busLamp5, new GridBagConstraints());
/*  78:    */     
/*  79: 75 */     this.busLamp6.setMaximumSize(new Dimension(26, 10));
/*  80: 76 */     this.busLamp6.setMinimumSize(new Dimension(13, 5));
/*  81: 77 */     this.busLamp6.setPreferredSize(new Dimension(26, 10));
/*  82: 78 */     add(this.busLamp6, new GridBagConstraints());
/*  83:    */     
/*  84: 80 */     this.busLamp7.setMaximumSize(new Dimension(26, 10));
/*  85: 81 */     this.busLamp7.setMinimumSize(new Dimension(13, 5));
/*  86: 82 */     this.busLamp7.setPreferredSize(new Dimension(26, 10));
/*  87: 83 */     add(this.busLamp7, new GridBagConstraints());
/*  88:    */     
/*  89: 85 */     this.busLamp8.setMaximumSize(new Dimension(26, 10));
/*  90: 86 */     this.busLamp8.setMinimumSize(new Dimension(13, 5));
/*  91: 87 */     this.busLamp8.setPreferredSize(new Dimension(26, 10));
/*  92: 88 */     add(this.busLamp8, new GridBagConstraints());
/*  93:    */     
/*  94: 90 */     this.busLamp9.setMaximumSize(new Dimension(26, 10));
/*  95: 91 */     this.busLamp9.setMinimumSize(new Dimension(13, 5));
/*  96: 92 */     this.busLamp9.setPreferredSize(new Dimension(26, 10));
/*  97: 93 */     add(this.busLamp9, new GridBagConstraints());
/*  98:    */     
/*  99: 95 */     this.busLamp10.setMaximumSize(new Dimension(26, 10));
/* 100: 96 */     this.busLamp10.setMinimumSize(new Dimension(13, 5));
/* 101: 97 */     this.busLamp10.setPreferredSize(new Dimension(26, 10));
/* 102: 98 */     add(this.busLamp10, new GridBagConstraints());
/* 103:    */     
/* 104:100 */     this.busLamp11.setMaximumSize(new Dimension(26, 10));
/* 105:101 */     this.busLamp11.setMinimumSize(new Dimension(13, 5));
/* 106:102 */     this.busLamp11.setPreferredSize(new Dimension(26, 10));
/* 107:103 */     add(this.busLamp11, new GridBagConstraints());
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setState(int state)
/* 111:    */   {
/* 112:112 */     if (this.state != state)
/* 113:    */     {
/* 114:113 */       this.state = state;
/* 115:114 */       this.busLamp0.setState(state & 0x800);
/* 116:115 */       this.busLamp1.setState(state & 0x400);
/* 117:116 */       this.busLamp2.setState(state & 0x200);
/* 118:117 */       this.busLamp3.setState(state & 0x100);
/* 119:118 */       this.busLamp4.setState(state & 0x80);
/* 120:119 */       this.busLamp5.setState(state & 0x40);
/* 121:120 */       this.busLamp6.setState(state & 0x20);
/* 122:121 */       this.busLamp7.setState(state & 0x10);
/* 123:122 */       this.busLamp8.setState(state & 0x8);
/* 124:123 */       this.busLamp9.setState(state & 0x4);
/* 125:124 */       this.busLamp10.setState(state & 0x2);
/* 126:125 */       this.busLamp11.setState(state & 0x1);
/* 127:    */     }
/* 128:    */   }
/* 129:    */   
/* 130:149 */   private int state = -1;
/* 131:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.BusLamps
 * JD-Core Version:    0.7.0.1
 */