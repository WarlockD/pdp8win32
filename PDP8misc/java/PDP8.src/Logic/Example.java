/*  1:   */ package Logic;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ 
/*  5:   */ public class Example
/*  6:   */   implements Device, Constants
/*  7:   */ {
/*  8:18 */   public static int DevId44 = 36;
/*  9:   */   public BusRegMem data;
/* 10:   */   private VirTimer xxxtim;
/* 11:   */   private boolean flag;
/* 12:   */   private boolean intena;
/* 13:   */   
/* 14:   */   public Example(BusRegMem data)
/* 15:   */   {
/* 16:29 */     this.data = data;
/* 17:   */     
/* 18:31 */     VirListener xxx = new VirListener()
/* 19:   */     {
/* 20:   */       public void actionPerformed()
/* 21:   */       {
/* 22:33 */         System.out.println("XXX");
/* 23:34 */         Example.this.setXXX(true);
/* 24:   */       }
/* 25:36 */     };
/* 26:37 */     this.xxxtim = new VirTimer(data.virqueue, 1400000, xxx);
/* 27:38 */     this.xxxtim.setRepeats(false);
/* 28:   */   }
/* 29:   */   
/* 30:   */   public void Decode(int devcode, int opcode) {}
/* 31:   */   
/* 32:   */   public void Execute(int devcode, int opcode) {}
/* 33:   */   
/* 34:   */   private void setCommand(int xdata) {}
/* 35:   */   
/* 36:   */   private int getCommand()
/* 37:   */   {
/* 38:55 */     return 0;
/* 39:   */   }
/* 40:   */   
/* 41:   */   private void setXXX(boolean set) {}
/* 42:   */   
/* 43:   */   public void clearIntReq()
/* 44:   */   {
/* 45:62 */     if (!this.flag) {
/* 46:63 */       this.data.setIntReq(DevId44, false);
/* 47:   */     }
/* 48:   */   }
/* 49:   */   
/* 50:   */   public void setIntReq()
/* 51:   */   {
/* 52:68 */     if (this.flag == true) {
/* 53:69 */       this.data.setIntReq(DevId44, this.intena);
/* 54:   */     }
/* 55:   */   }
/* 56:   */   
/* 57:   */   public void ClearFlags(int devcode)
/* 58:   */   {
/* 59:74 */     this.data.setIntReq(DevId44, false);
/* 60:   */   }
/* 61:   */   
/* 62:   */   public void Interrupt(int command) {}
/* 63:   */   
/* 64:   */   public void ClearRun(boolean run) {}
/* 65:   */   
/* 66:   */   public void CloseDev(int devcode) {}
/* 67:   */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.Example
 * JD-Core Version:    0.7.0.1
 */