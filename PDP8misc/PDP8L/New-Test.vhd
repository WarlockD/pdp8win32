--
--
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;


entity SystemTest is

end;



architecture Test of SystemTest is

Component PDP8L is
     Port (  Tx_out : out STD_LOGIC;
			  Rx_in : in STD_LOGIC;
			  Clk : in STD_LOGIC;
			  Reset : in STD_LOGIC;
			  Led : out STD_LOGIC_VECTOR (3 downto 0)
			  );
end component;

signal        Reset  : std_logic:='0';
signal        Tx_out  : std_logic:='0';
signal        Rx_in : std_logic:='1';
signal        Led  : std_logic_vector(3 downto 0):=b"0000";
signal        Clk  : std_logic:='0';


begin
  
CPU1 : PDP8L 
      Port MAP(Tx_out,rx_in,Clk,Reset,Led);

  
clock : PROCESS
begin
   wait for 10 ns; Clk  <= not Clk;
end PROCESS clock;

stimulus : PROCESS
   begin
   wait for 4 ns; Reset  <= '0';
   wait for 8 ns; Reset  <= '1';
   wait;
end PROCESS stimulus;

end Test;