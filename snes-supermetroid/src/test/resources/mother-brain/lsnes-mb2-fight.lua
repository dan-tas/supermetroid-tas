--[[
reset-lua
run-lua src\test\resources\mother-brain\lsnes-mb2-fight.lua
]]--

local WRAM="WRAM";
local ADDR_RNG = 0x05E5;
local ADDR_SAMUS_MOVE_TYPE = 0x0A1F;
local ADDR_SAMUS_YPOS_PIXEL = 0x0AFA;
local ADDR_BEAM_CHARGE = 0x0CD0;
local ADDR_MB2_HEALTH = 0x0FCC;
local ADDR_NME00_XPOS_PIXEL = 0x0F7A;
local ADDR_NME00_AI_PTR = 0x0FA8;
local ADDR_NME00_ROUTINE_COUNTER = 0x0FB2;
local ADDR_NME00_AI_TIMER = 0x0FB4;
local ADDR_NME00_DELAY_COUNTER = 0x0F94;
local ADDR_NME01_YPOS_PIXEL = 0x0FBE;
local ADDR_MB2_IS_BUSY = 0x7804;
local ADDR_MB2_LOOP_DELAY_COUNTER = 0x0780E;
local ADDR_MB2_AI_ATTACK_INDEX = 0x7830;
local ADDR_MB2_MEATBALL_ON_SCREEN = 0x784A;


function on_frame_emulated()
  print(
    string.format("%06d %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X %04X", 
    movie.currentframe()+1, 
    memory.readword(WRAM, ADDR_BEAM_CHARGE), 
    memory.readword(WRAM, ADDR_MB2_HEALTH), 
    memory.readword(WRAM, ADDR_RNG),
    memory.readword(WRAM, ADDR_MB2_IS_BUSY),
    memory.readword(WRAM, ADDR_NME00_AI_PTR),
    memory.readword(WRAM, ADDR_NME00_ROUTINE_COUNTER),
    memory.readword(WRAM, ADDR_NME00_AI_TIMER),
    memory.readword(WRAM, ADDR_NME00_DELAY_COUNTER),
    memory.readword(WRAM, ADDR_MB2_LOOP_DELAY_COUNTER),
    memory.readword(WRAM, ADDR_MB2_AI_ATTACK_INDEX),
    memory.readword(WRAM, ADDR_MB2_MEATBALL_ON_SCREEN),
    memory.readword(WRAM, ADDR_NME00_XPOS_PIXEL),
    memory.readword(WRAM, ADDR_NME01_YPOS_PIXEL),
    memory.readword(WRAM, ADDR_SAMUS_YPOS_PIXEL),
    memory.readword(WRAM, ADDR_SAMUS_MOVE_TYPE)
  ));
end
