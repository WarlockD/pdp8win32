package Logic;

public abstract interface Device
{
  public static final int INTINPROG = 1;
  public static final int JMPJMS = 2;
  public static final int USERMODE = 3;
  public static final int MAPIFR = 4;
  public static final int MAPDFR = 5;
  
  public abstract void Decode(int paramInt1, int paramInt2);
  
  public abstract void Execute(int paramInt1, int paramInt2);
  
  public abstract void ClearFlags(int paramInt);
  
  public abstract void ClearRun(boolean paramBoolean);
  
  public abstract void CloseDev(int paramInt);
  
  public abstract void Interrupt(int paramInt);
}


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.Device
 * JD-Core Version:    0.7.0.1
 */