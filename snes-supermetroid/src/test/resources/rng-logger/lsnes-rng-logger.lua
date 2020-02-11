--[[
reset-lua
run-lua run-lua src\test\resources\rng-logger\lsnes-rng-logger.lua
]]--

function on_frame()
  print(string.format("%06d %04X", movie.currentframe(), memory.readword("WRAM", 0x05E5)));
end;