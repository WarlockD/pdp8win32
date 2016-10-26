/*   1:    */ package Panel;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.GridBagConstraints;
/*   5:    */ import java.awt.GridBagLayout;
/*   6:    */ import javax.swing.JLabel;
/*   7:    */ import javax.swing.JTextArea;
/*   8:    */ 
/*   9:    */ public class JPanel
/*  10:    */   extends javax.swing.JPanel
/*  11:    */ {
/*  12:    */   private JLabel jLabel1;
/*  13:    */   private JLabel jLabel2;
/*  14:    */   private JTextArea jTextArea1;
/*  15:    */   private Lamp lamp1;
/*  16:    */   private Lamp lamp2;
/*  17:    */   private Lamp lamp3;
/*  18:    */   private Lamp lamp4;
/*  19:    */   private Lamp lamp5;
/*  20:    */   private Lamp lamp6;
/*  21:    */   private Lamp lamp7;
/*  22:    */   
/*  23:    */   public JPanel()
/*  24:    */   {
/*  25: 18 */     initComponents();
/*  26:    */   }
/*  27:    */   
/*  28:    */   private void initComponents()
/*  29:    */   {
/*  30: 29 */     this.lamp1 = new Lamp();
/*  31: 30 */     this.lamp2 = new Lamp();
/*  32: 31 */     this.lamp3 = new Lamp();
/*  33: 32 */     this.lamp4 = new Lamp();
/*  34: 33 */     this.jTextArea1 = new JTextArea();
/*  35: 34 */     this.jLabel1 = new JLabel();
/*  36: 35 */     this.jLabel2 = new JLabel();
/*  37: 36 */     this.lamp5 = new Lamp();
/*  38: 37 */     this.lamp6 = new Lamp();
/*  39: 38 */     this.lamp7 = new Lamp();
/*  40:    */     
/*  41: 40 */     setLayout(new GridBagLayout());
/*  42:    */     
/*  43: 42 */     setPreferredSize(new Dimension(300, 215));
/*  44: 43 */     this.lamp1.setMaximumSize(new Dimension(20, 20));
/*  45: 44 */     this.lamp1.setMinimumSize(new Dimension(20, 20));
/*  46: 45 */     this.lamp1.setPreferredSize(new Dimension(30, 15));
/*  47: 46 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*  48: 47 */     gridBagConstraints.gridx = 1;
/*  49: 48 */     gridBagConstraints.gridy = 1;
/*  50: 49 */     add(this.lamp1, gridBagConstraints);
/*  51:    */     
/*  52: 51 */     this.lamp2.setMaximumSize(new Dimension(20, 20));
/*  53: 52 */     this.lamp2.setMinimumSize(new Dimension(20, 20));
/*  54: 53 */     this.lamp2.setPreferredSize(new Dimension(30, 15));
/*  55: 54 */     gridBagConstraints = new GridBagConstraints();
/*  56: 55 */     gridBagConstraints.gridy = 1;
/*  57: 56 */     add(this.lamp2, gridBagConstraints);
/*  58:    */     
/*  59: 58 */     this.lamp3.setMinimumSize(new Dimension(20, 20));
/*  60: 59 */     this.lamp3.setPreferredSize(new Dimension(30, 15));
/*  61: 60 */     gridBagConstraints = new GridBagConstraints();
/*  62: 61 */     gridBagConstraints.gridy = 1;
/*  63: 62 */     add(this.lamp3, gridBagConstraints);
/*  64:    */     
/*  65: 64 */     this.lamp4.setMinimumSize(new Dimension(20, 20));
/*  66: 65 */     this.lamp4.setPreferredSize(new Dimension(30, 15));
/*  67: 66 */     gridBagConstraints = new GridBagConstraints();
/*  68: 67 */     gridBagConstraints.gridy = 1;
/*  69: 68 */     add(this.lamp4, gridBagConstraints);
/*  70:    */     
/*  71: 70 */     this.jTextArea1.setPreferredSize(new Dimension(300, 200));
/*  72: 71 */     gridBagConstraints = new GridBagConstraints();
/*  73: 72 */     gridBagConstraints.gridx = 0;
/*  74: 73 */     gridBagConstraints.gridy = 0;
/*  75: 74 */     gridBagConstraints.gridwidth = 9;
/*  76: 75 */     add(this.jTextArea1, gridBagConstraints);
/*  77:    */     
/*  78: 77 */     this.jLabel1.setText("jLabel1");
/*  79: 78 */     gridBagConstraints = new GridBagConstraints();
/*  80: 79 */     gridBagConstraints.gridx = 0;
/*  81: 80 */     gridBagConstraints.gridy = 1;
/*  82: 81 */     add(this.jLabel1, gridBagConstraints);
/*  83:    */     
/*  84: 83 */     this.jLabel2.setText("jLabel1");
/*  85: 84 */     gridBagConstraints = new GridBagConstraints();
/*  86: 85 */     gridBagConstraints.gridx = 8;
/*  87: 86 */     gridBagConstraints.gridy = 1;
/*  88: 87 */     add(this.jLabel2, gridBagConstraints);
/*  89:    */     
/*  90: 89 */     this.lamp5.setMinimumSize(new Dimension(20, 20));
/*  91: 90 */     this.lamp5.setPreferredSize(new Dimension(30, 15));
/*  92: 91 */     gridBagConstraints = new GridBagConstraints();
/*  93: 92 */     gridBagConstraints.gridx = 5;
/*  94: 93 */     gridBagConstraints.gridy = 1;
/*  95: 94 */     add(this.lamp5, gridBagConstraints);
/*  96:    */     
/*  97: 96 */     this.lamp6.setMinimumSize(new Dimension(20, 20));
/*  98: 97 */     this.lamp6.setPreferredSize(new Dimension(30, 15));
/*  99: 98 */     gridBagConstraints = new GridBagConstraints();
/* 100: 99 */     gridBagConstraints.gridy = 1;
/* 101:100 */     add(this.lamp6, gridBagConstraints);
/* 102:    */     
/* 103:102 */     this.lamp7.setMinimumSize(new Dimension(20, 20));
/* 104:103 */     this.lamp7.setPreferredSize(new Dimension(30, 15));
/* 105:104 */     gridBagConstraints = new GridBagConstraints();
/* 106:105 */     gridBagConstraints.gridy = 1;
/* 107:106 */     add(this.lamp7, gridBagConstraints);
/* 108:    */   }
/* 109:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.JPanel
 * JD-Core Version:    0.7.0.1
 */