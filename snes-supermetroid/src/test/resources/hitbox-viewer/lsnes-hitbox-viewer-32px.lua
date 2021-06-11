--[[
reset-lua
run-lua src\test\resources\hitbox-viewer\lsnes-hitbox-viewer-32px.lua
]]

local DEBUG_FLAG = false;

local WRAM = 'WRAM';
local ROM = 'ROM';

-- Each bank is from 0x8000 to 0xFFFF so remove one extra 0x8000
local BANK_OFFSET_86 = bit.lshift((0x86 - 0x80 - 0x01), 15);
local BANK_OFFSET_A0 = bit.lshift((0xA0 - 0x80 - 0x01), 15);

local BLOCK_SIZE = 0x10;
local BLOCK_SIZE_DISPLAY = 2 * BLOCK_SIZE;

local SCREEN_X_ENEMY_SUMMARY = 2 * 0xE0;

local SCREEN_X_BLOCKS_HALF = 8;
local SCREEN_X_BLOCKS_DISPLAY = (2 * SCREEN_X_BLOCKS_HALF) + 1;
local SCREEN_X_HALF = SCREEN_X_BLOCKS_HALF * BLOCK_SIZE;
local SCREEN_X_DISPLAY = 2 * 2 * SCREEN_X_HALF;

local SCREEN_Y_BLOCKS_HALF = 7;
local SCREEN_Y_BLOCKS_DISPLAY = (2 * SCREEN_Y_BLOCKS_HALF) + 1;
local SCREEN_Y_HALF = SCREEN_Y_BLOCKS_HALF * BLOCK_SIZE;
local SCREEN_Y_FULL = 2 * SCREEN_Y_HALF;

local BLOCKS_DISPLAY = (SCREEN_X_BLOCKS_DISPLAY * SCREEN_Y_BLOCKS_DISPLAY);

local ENEMY_HEALTH_BAR_WIDTH = 2 * 0x20;
local ENEMY_HEALTH_BAR_HEIGHT = 2 * 3;


local COLOR_TRANSPARENT = -1; -- -1 is transparent for fill color, not sure this also works for border color

local COLOR_ROOM_LIQUID_WATER = 0x2020E0;
local COLOR_ROOM_LIQUID_LAVA = 0xA0A000;
local COLOR_ROOM_LIQUID_ACID = 0xA05080; -- purple/gray because red is hard to see against red solid blocks

local COLOR_ROOM_BLOCK_BOMB = 0x0000FF;
local COLOR_ROOM_BLOCK_CRUMBLE = 0x0000FF;
local COLOR_ROOM_BLOCK_DOOR = 0xFFA500;
local COLOR_ROOM_BLOCK_EXTENSION = 0xFF00FF;
local COLOR_ROOM_BLOCK_EXTENSION_DEFAULT = 0xC0C0C0;
local COLOR_ROOM_BLOCK_GRAPPLE = 0x0000FF;
local COLOR_ROOM_BLOCK_NORMAL = 0xFF0000;
local COLOR_ROOM_BLOCK_SHOT = 0x0000FF;
local COLOR_ROOM_BLOCK_SLOPE = 0xFFFF00;
local COLOR_ROOM_BLOCK_SPIKE = 0x0000FF;
local COLOR_ROOM_BLOCK_TRANSITION = 0x00FFFF;
local COLOR_ROOM_BLOCK_TREADMILL = 0x80FF40;

local COLOR_ROOM_DEBUG_BLOCK_TYPE = 0xFF0000;
local COLOR_ROOM_DEBUG_BLOCK_PROPS = 0xFFA500;

local COLOR_ENEMY = 0x808080;
local COLOR_ENEMY_HEALTH_BAR = 0x606060;
local COLOR_ENEMY_PROJECTILE = 0x00FF00; -- includes item drops

local COLOR_SAMUS_PROJECTILES = 0xFFFF80;
local COLOR_SAMUS_BOMBS = 0xFFFF80;

local COLOR_SAMUS_HITBOX_FASTEST = 0x00FF00;
local COLOR_SAMUS_HITBOX_FASTER  = 0x80FFFF;
local COLOR_SAMUS_HITBOX_AVERAGE = 0xFF8000;
local COLOR_SAMUS_HITBOX_SLOWER  = 0xFF0000;
local COLOR_SAMUS_HITBOX_SLOWEST = 0x800000;

local SAMUS_X_LAST = 0x00000000;
local SAMUS_X_NEXT = 0x00000000;
function on_frame()
  SAMUS_X_LAST = samus_x_position();
end;
function on_frame_emulated()
  SAMUS_X_NEXT = samus_x_position();
end;
function samus_x_position()
  return (bit.lshift(memory.readword(WRAM, 0x0AF6), 0x10) + memory.readword(WRAM, 0x0AF8));
end;

function on_paint()
  if (draw_everything())
  then
    local samusX = memory.readword(WRAM, 0x0AF6);
    local samusY = memory.readword(WRAM, 0x0AFA);

    local cameraX = (samusX - SCREEN_X_HALF);
    local cameraY = (samusY - SCREEN_Y_HALF);

    draw_room(cameraX, cameraY, samusX, samusY);
    draw_enemies(cameraX, cameraY);
    draw_samus(cameraX, cameraY);
  end;
end;
function draw_everything()
  local emulatorSpeed = settings.get_speed();
  local fastForward = (((type(emulatorSpeed) == "number") and (emulatorSpeed > 1.5)) or (type(emulatorSpeed) == "string")) -- "turbo"
  if (fastForward)
  then
    return false;
  end;

  local gameMode = memory.readbyte(WRAM, 0x0998);
  return ((gameMode == 0x08) or (gameMode == 0x09) or (gameMode == 0x1B)) -- active gameplay, hit door transition, or reserve tanks emptying
end;

 function draw_room(cameraX, cameraY, samusX, samusY)
  draw_room_tiles(cameraX, cameraY, samusX, samusY);
  draw_room_liquid_level(cameraY);
end;
function draw_room_liquid_level(cameraY)
  local liquidLevelAbsolute;
  local liquidColor;

  local fxType = memory.readbyte(WRAM, 0x196E);
  if ((bit.band(fxType, 0xF9) == 0x00) and (bit.band(fxType, 0x06) ~= 0x00))
  then
    if (fxType == 0x06)
    then
      liquidLevelAbsolute = memory.readword(WRAM, 0x195E);
      liquidColor = COLOR_ROOM_LIQUID_WATER;
    else
      liquidLevelAbsolute = memory.readword(WRAM, 0x1962);
      if (fxType == 0x04)
      then
        liquidColor = COLOR_ROOM_LIQUID_ACID;
      else
        liquidColor = COLOR_ROOM_LIQUID_LAVA;
      end;
    end;

    liquidLevelRelative = bit.lshift((liquidLevelAbsolute - cameraY), 1);
    gui.line(0, liquidLevelRelative, SCREEN_X_DISPLAY, liquidLevelRelative, liquidColor);
  end;
end;
function draw_room_tiles(cameraX, cameraY, samusX, samusY)
  local cameraOffsetX = bit.lshift(bit.band(cameraX, 0x000F), 1);
  local cameraOffsetY = bit.lshift(bit.band(cameraY, 0x000F), 1);

  local samusBlockX = bit.lrshift(samusX, 4);
  local samusBlockY = bit.lrshift(samusY, 4);

  local roomWidth = memory.readword(WRAM, 0x07A5);
  local samusBlockOffset = ((roomWidth * samusBlockY) + samusBlockX);

  local dataOffset = (bit.lrshift(roomWidth, 4) + 1);
  local cameraBlockOffset = (bit.lshift((roomWidth + 1), 3) - bit.lshift(dataOffset, 4)); -- 16 = (SCREEN_X_BLOCKS_DISPLAY - 1)

  local blockOffset = (samusBlockOffset - cameraBlockOffset);
  local blockRowOffset = roomWidth - (SCREEN_X_BLOCKS_DISPLAY - 1);


  local cameraBlockX = bit.band((samusBlockX - SCREEN_X_BLOCKS_HALF), 0xFFF);
  local cameraBlockY = bit.band((samusBlockY - SCREEN_Y_BLOCKS_HALF), 0xFFF);
  gui.text(0, 0, string.format("camera: <%03X,%03X>\noffset: %05X", cameraBlockX, cameraBlockY, bit.band(blockOffset, 0xFFFFF)));

  local cameraTileOffsetX = -cameraOffsetX;
  local cameraTileOffsetY = -cameraOffsetY;
  for cameraTileNumber=0,(BLOCKS_DISPLAY - 1)
  do
    draw_room_tile(cameraTileOffsetX, cameraTileOffsetY, blockOffset, roomWidth);

    if ((cameraTileNumber % SCREEN_X_BLOCKS_DISPLAY) ~= 0)
    then
      cameraTileOffsetX = cameraTileOffsetX + BLOCK_SIZE_DISPLAY;
      blockOffset = blockOffset + 1;
    else
      cameraTileOffsetX = -cameraOffsetX;
      cameraTileOffsetY = cameraTileOffsetY + BLOCK_SIZE_DISPLAY;
      blockOffset = blockOffset + blockRowOffset;
    end;
  end
end;

function draw_room_tile(cameraTileOffsetX, cameraTileOffsetY, blockOffset, roomWidth)
  local blockType = block_type(blockOffset);
  local blockProperties = block_properties(blockOffset);

  if (DEBUG_FLAG or (blockType == 0x02) or (blockType == 0x09))
  then
    gui.text(cameraTileOffsetX+8, cameraTileOffsetY+1, string.format("%02X", blockType) , COLOR_ROOM_DEBUG_BLOCK_TYPE);
    gui.text(cameraTileOffsetX+8, cameraTileOffsetY+12, string.format("%02X", blockProperties), COLOR_ROOM_DEBUG_BLOCK_PROPS);
  end

  local blockColor = block_color[blockType](blockProperties, blockOffset, roomWidth, 12);
  if ((blockType == 0x01) or (blockType == 0x05) or (blockType == 0x0D))
  then
    if (blockType == 0x01)
    then -- slope tile
      local vFlip = bit.test(blockProperties, 7);
      local hFlip = bit.test(blockProperties, 6);

      local sf = bit.band(blockProperties, 0x1F);
      slope[sf](cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip);
    else -- extender tile
      draw_tile_boundaries(cameraTileOffsetX, cameraTileOffsetY, blockColor);
      draw_tile_nested(cameraTileOffsetX, cameraTileOffsetY, COLOR_ROOM_BLOCK_EXTENSION);
    end;
  else
    if ((blockType ~= 0x00) and (blockType ~= 0x02) and (blockType ~= 0x06) and (blockType ~= 0x07)) -- these four are unused or nothing to draw
    then
      draw_tile_boundaries(cameraTileOffsetX, cameraTileOffsetY, blockColor);
      if (blockType == 0x08) -- solid tile
      then
        draw_tile_nested(cameraTileOffsetX, cameraTileOffsetY, blockColor);
      end;
    end;
  end;
end;

block_color = {
  [0x00] = function () return COLOR_TRANSPARENT          ; end, -- Air
  [0x01] = function () return COLOR_ROOM_BLOCK_SLOPE     ; end, -- Slope
  [0x02] = function () return COLOR_TRANSPARENT          ; end, -- Air, tricks X-ray
  [0x03] = function () return COLOR_ROOM_BLOCK_TREADMILL ; end, -- Treadmill
  [0x04] = function () return COLOR_TRANSPARENT          ; end, -- unused?
  [0x05] = function (blockProperties, blockOffset, roomWidth, recursiveCount) -- Horizontal extend
    if (recursiveCount == 0x00)
    then
      return COLOR_ROOM_BLOCK_EXTENSION_DEFAULT; -- Without a halting condition, these blocks can loop infinitely
    end;

    local relativeBlockOffset = (blockOffset + sign_extend_byte(blockProperties));

    local relativeBlockType = block_type(relativeBlockOffset);
    local relativeBlockProperties = block_properties(relativeBlockOffset);

    return block_color[relativeBlockType](relativeBlockProperties, relativeBlockOffset, roomWidth, (recursiveCount - 1));
  end,
  [0x06] = function () return COLOR_TRANSPARENT          ; end, -- unused?
  [0x07] = function () return COLOR_TRANSPARENT          ; end, -- unused?
  [0x08] = function () return COLOR_ROOM_BLOCK_NORMAL    ; end, -- Normal block
  [0x09] = function () return COLOR_ROOM_BLOCK_TRANSITION; end, -- Transition block
  [0x0A] = function () return COLOR_ROOM_BLOCK_SPIKE     ; end, -- Spike block
  [0x0B] = function () return COLOR_ROOM_BLOCK_CRUMBLE   ; end, -- Crumble block
  [0x0C] = function (blockProperties, blockOffset, roomWidth) -- Shot block
    if ((0x40 <= blockProperties) and (blockProperties <= 0x43))
    then
        return COLOR_ROOM_BLOCK_DOOR;
      else
        return COLOR_ROOM_BLOCK_SHOT;
      end;
  end,
  [0x0D] = function (blockProperties, blockOffset, roomWidth, recursiveCount) -- Vertical extend
    if (recursiveCount == 0x00)
    then
      return COLOR_ROOM_BLOCK_EXTENSION_DEFAULT;
    end;

    local relativeBlockOffset = (blockOffset + (sign_extend_byte(blockProperties) * roomWidth));
    local relativeBlockType = block_type(relativeBlockOffset);
    local relativeBlockProperties = block_properties(relativeBlockOffset);

    return block_color[relativeBlockType](relativeBlockProperties, relativeBlockOffset, roomWidth, (recursiveCount -1));
  end,
  [0x0E] = function () return COLOR_ROOM_BLOCK_GRAPPLE   ; end, -- Grapple block
  [0x0F] = function () return COLOR_ROOM_BLOCK_BOMB      ; end  -- Bomb block
}
function block_type(blockOffset)
  local clipOffset = bit.band(0x10002 + bit.lshift(blockOffset, 1), 0xFFFFF);
  local blockDefinition = memory.readword(WRAM, clipOffset);
  return bit.lrshift(blockDefinition, 0x0C);
end;
function block_properties(blockOffset)
  local btsOffset = bit.band((0x16402 + blockOffset), 0xFFFFF);
  return memory.readbyte(WRAM, btsOffset);
end;
function sign_extend_byte(byte)
  if (bit.test(byte, 7))
  then
    return (0xFFFFFFFFFFFFFF00 + byte)
  else
    return byte;
  end;
end;

function draw_tile_boundaries(cameraTileOffsetX, cameraTileOffsetY, blockColor)
  gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+     0x1F        , cameraTileOffsetX+     0x00        , cameraTileOffsetY+     0x1F        , blockColor);
  gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+     0x1F        , cameraTileOffsetX+     0x00        , cameraTileOffsetY+     0x00        , blockColor);
  gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+     0x00        , cameraTileOffsetX+     0x1F        , cameraTileOffsetY+     0x00        , blockColor);
  gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+     0x00        , cameraTileOffsetX+     0x1F        , cameraTileOffsetY+     0x1F        , blockColor);
end;
function draw_tile_nested(cameraTileOffsetX, cameraTileOffsetY, blockColor)
  gui.line(cameraTileOffsetX+     0x1C        , cameraTileOffsetY+     0x1C        , cameraTileOffsetX+     0x03        , cameraTileOffsetY+     0x1C        , blockColor);
  gui.line(cameraTileOffsetX+     0x03        , cameraTileOffsetY+     0x1C        , cameraTileOffsetX+     0x03        , cameraTileOffsetY+     0x03        , blockColor);
  gui.line(cameraTileOffsetX+     0x03        , cameraTileOffsetY+     0x03        , cameraTileOffsetX+     0x1C        , cameraTileOffsetY+     0x03        , blockColor);
  gui.line(cameraTileOffsetX+     0x1C        , cameraTileOffsetY+     0x03        , cameraTileOffsetX+     0x1C        , cameraTileOffsetY+     0x1C        , blockColor);
end;
function default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip)
  draw_tile_boundaries(cameraTileOffsetX, cameraTileOffsetY, COLOR_ROOM_BLOCK_SLOPE);

  gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x04, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x04, vFlip), COLOR_ROOM_BLOCK_SLOPE);
end;
function flip(pixel, flip)
  if (not flip)
  then
    return pixel;
  else
    return (0x1F - pixel);
  end;
end;
slope = {
  [0x00] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1/2 tile rectangle, 100% wide 50% tall, covers bottom by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
  end,
  [0x01] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1/2 tile rectangle, 50% wide 100% tall, covers right by default
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+     0x1F        , cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+     0x00        , blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x02] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1/4 tile square, 50% wide 50% tall, covers bottom right by default
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
  end,
  [0x03] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 3/4 tile anti-square, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x04] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- unused?
    draw_tile_boundaries(cameraTileOffsetX, cameraTileOffsetY, blockColor);
  end,
  [0x05] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1/4 tile triangle, 100% wide 50% tall, covers bottom by default, horizontally symmetric
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x0F        , cameraTileOffsetY+flip(0x10, vFlip), blockColor); -- this line becomes the 3rd line when hFlip is 1
    gui.line(cameraTileOffsetX+     0x10        , cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor); -- this line becomes the 2nd line when hFlip is 1
  end,
  [0x06] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1/2 tile triangle, covers bottom by default, 100% wide 50% tall, covers bottom by default, horizontally symmetric
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x0F        , cameraTileOffsetY+flip(0x00, vFlip), blockColor); -- this line becomes the 3rd line when hFlip is 1
    gui.line(cameraTileOffsetX+     0x10        , cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor); -- this line becomes the 2nd line when hFlip is 1
  end,
  [0x07] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1/2 tile unmagnet, pushes up by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);

    gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x14, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x14, vFlip), blockColor);
  end,
  [0x08] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- is this used?
    default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip);
  end,
  [0x09] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- is this used?
    default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip);
  end,
  [0x0A] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- is this used?
    default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip);
  end,
  [0x0B] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- is this used?
    default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip);
  end,
  [0x0C] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- is this used?
    default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip);
  end,
  [0x0D] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- is this used?
    default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip);
  end,
  [0x0E] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- four step bars, solid for (4+3+2+1)/(4+4+4+4), covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x18, vFlip), blockColor); -- this line becomes the 4th line when hFlip is 1
    gui.line(cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x18, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x18, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x18, vFlip), cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor); -- this line becomes the 2nd line when hFlip is 1

    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x17, vFlip), cameraTileOffsetX+flip(0x08, hFlip), cameraTileOffsetY+flip(0x17, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x08, hFlip), cameraTileOffsetY+flip(0x17, vFlip), cameraTileOffsetX+flip(0x08, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x08, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x17, vFlip), blockColor);

    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x0F, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x0F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x0F, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x08, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x08, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x08, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x08, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x0F, vFlip), blockColor);

    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x07, vFlip), cameraTileOffsetX+flip(0x18, hFlip), cameraTileOffsetY+flip(0x07, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x18, hFlip), cameraTileOffsetY+flip(0x07, vFlip), cameraTileOffsetX+flip(0x18, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x18, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x07, vFlip), blockColor);
  end,
  [0x0F] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- tourian escape room 3 stairs
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x0F, hFlip), cameraTileOffsetY+flip(0x18, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x17, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x10] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- four horizontal lines, evenly spaced, unused?
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x17, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x17, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x0F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x0F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x07, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x07, vFlip), blockColor);
  end,
  [0x11] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- four vertical lines, evenly spaced, unused?
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
    gui.line(cameraTileOffsetX+flip(0x17, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x17, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
    gui.line(cameraTileOffsetX+flip(0x0F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x0F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
    gui.line(cameraTileOffsetX+flip(0x07, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x07, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x12] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x1 diagonal, 1/2 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x13] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- full tile unmagnet, pushes up by default
    default_slope(cameraTileOffsetX, cameraTileOffsetY, vFlip);
  end,
  [0x14] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x1 diagonal, 1/4 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
  end,
  [0x15] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x1 diagonal, 3/4 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x0F, vFlip), cameraTileOffsetX+flip(0x0F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x16] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 2x1 diagonal, wider than it is tall, 1/4 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x10, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
  end,
  [0x17] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 2x1 diagonal, wider than it is tall, 3/4 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x10, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x0F, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x18] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 3x1 diagonal, wider than it is tall, 1/6 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x15, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x15, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
  end,
  [0x19] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 3x1 diagonal, wider than it is tall, 3/6 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x15, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x15, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x0A, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x0A, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
  end,
  [0x1A] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 3x1 diagonal, wider than it is tall, 5/6 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x0A, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x0A, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x1B] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x2 diagonal, taller than it is wide, 1/4 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x1C] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x2 diagonal, taller than it is wide, 3/4 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x0F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x10, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x1D] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x3 diagonal, taller than it is wide, 1/6 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x15, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x15, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x1E] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x3 diagonal, taller than it is wide, 3/6 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x0A, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x0A, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x15, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x15, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end,
  [0x1F] = function (cameraTileOffsetX, cameraTileOffsetY, blockColor, hFlip, vFlip) -- 1x3 diagonal, taller than it is wide, 5/6 solid, covers bottom right by default
    gui.line(cameraTileOffsetX+     0x1F        , cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+     0x00        , cameraTileOffsetY+flip(0x1F, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x00, hFlip), cameraTileOffsetY+flip(0x1F, vFlip), cameraTileOffsetX+flip(0x0A, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x0A, hFlip), cameraTileOffsetY+flip(0x00, vFlip), cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+flip(0x00, vFlip), blockColor);
    gui.line(cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x00        , cameraTileOffsetX+flip(0x1F, hFlip), cameraTileOffsetY+     0x1F        , blockColor);
  end
}


function draw_enemies(cameraX, cameraY)
  draw_enemy_projectiles(cameraX, cameraY);

  local enemyCount = memory.readbyte(WRAM, 0x0E4E);
  if (enemyCount > 0)
  then
    for enemyNumber=0,(enemyCount-1)
    do
      draw_enemy(enemyNumber, cameraX, cameraY);
    end;
  end;
end;

function draw_enemy_projectiles(cameraX, cameraY) -- this includes item drops as well
  for enemyProjectileOffset=0,34,2
  do
    local enemyProjectileId = memory.readword(WRAM, (enemyProjectileOffset + 0x1997));
    local enemyProjectileTimer = memory.readword(WRAM, (enemyProjectileOffset + 0x19DF));
    local enemyProjectilePreInstruction = memory.readword(WRAM, (enemyProjectileOffset + 0x1A03)); -- item drop collected if preInstruction is 0xEFDF
    if ((enemyProjectileId ~= 0) and (enemyProjectileTimer == 0) and (enemyProjectilePreInstruction ~= 0xEFDF))
    then
      local header = memory.readword(WRAM, (enemyProjectileOffset + 0x1997));

      local enemyProjectileX = memory.readword(WRAM, (enemyProjectileOffset + 0x1A4B));
      local enemyProjectileY = memory.readword(WRAM, (enemyProjectileOffset + 0x1A93));

      local enemyProjectileRadiusX = memory.readbyte(ROM, (BANK_OFFSET_86 + header + 6));
      local enemyProjectileRadiusY = memory.readbyte(ROM, (BANK_OFFSET_86 + header + 7));

      local enemyProjectileLeft   = bit.lshift((enemyProjectileX - cameraX - enemyProjectileRadiusX), 1);
      local enemyProjectileTop    = bit.lshift((enemyProjectileY - cameraY - enemyProjectileRadiusY), 1);
      local enemyProjectileWidth  = bit.lshift(enemyProjectileRadiusX, 2);
      local enemyProjectileHeight = bit.lshift(enemyProjectileRadiusY, 2);

      gui.rectangle(enemyProjectileLeft, enemyProjectileTop, enemyProjectileWidth, enemyProjectileHeight, 1, COLOR_ITEM_DROP, -1);
    end;
  end;
end;

function draw_enemy(enemyNumber, cameraX, cameraY)
  local enemyOffset = bit.lshift(enemyNumber, 6);
  local enemyId = memory.readword(WRAM, (enemyOffset + 0x0F78));

  if (enemyId ~= 0)
  then
    local enemyX = memory.readword(WRAM, (enemyOffset + 0x0F7A));
    local enemyY = memory.readword(WRAM, (enemyOffset + 0x0F7E));

    local enemyRadiusX = memory.readword(WRAM, (enemyOffset + 0x0F82));
    local enemyRadiusY = memory.readword(WRAM, (enemyOffset + 0x0F84));

    local enemyRelativeX = (enemyX - cameraX);
    local enemyRelativeY = (enemyY - cameraY);

    local enemyLeft   = bit.lshift((enemyRelativeX - enemyRadiusX), 1);
    local enemyTop    = bit.lshift((enemyRelativeY - enemyRadiusY), 1);
    local enemyWidth  = bit.lshift(enemyRadiusX, 2);
    local enemyHeight = bit.lshift(enemyRadiusY, 2);

    draw_enemy_hitboxes(enemyOffset, enemyLeft, enemyTop, enemyWidth, enemyHeight, enemyRelativeX, enemyRelativeY);
    draw_enemy_data(enemyNumber, enemyOffset, enemyId, enemyLeft, enemyTop);
  end;
end;

function draw_enemy_hitboxes(enemyOffset, enemyLeft, enemyTop, enemyWidth, enemyHeight, enemyRelativeX, enemyRelativeY)
  local enemyProperties = memory.readbyte(WRAM, (enemyOffset + 0x0F88));
  local enemyAiHandler = memory.readbyte(WRAM, (enemyOffset + 0x0F8A));

  if (bit.testn(enemyProperties, 2) or (enemyAiHandler == 4))
  then
    gui.rectangle(enemyLeft, enemyTop, enemyWidth, enemyHeight, 1, COLOR_ENEMY, -1);
  else
    draw_enemy_oam_hitboxes(enemyOffset, enemyRelativeX, enemyRelativeY);
  end;
end;
function draw_enemy_oam_hitboxes(enemyOffset, enemyRelativeX, enemyRelativeY)
  local oamHitboxPointer = memory.readword(WRAM, (enemyOffset + 0x0F8E));
  if (oamHitboxPointer ~= 0)
  then
    local bank = memory.readbyte(WRAM, (enemyOffset + 0x0FA6));
    local bankOffset = bit.lshift((bank - 0x80 - 0x01), 15);
    oamHitboxPointer = oamHitboxPointer + bankOffset;
    local oamHitboxCount = memory.readbyte(ROM, oamHitboxPointer);

    if (oamHitboxCount ~= 0)
    then
      for oamHitbox=0,(oamHitboxCount-1)
      do
        draw_enemy_oam_hitbox(oamHitboxPointer, oamHitbox, bankOffset, enemyRelativeX, enemyRelativeY);
      end;
    end;
  end;
end;
function draw_enemy_oam_hitbox(oamHitboxPointer, oamHitbox, bankOffset, enemyRelativeX, enemyRelativeY)
  local entryPointer = (bit.lshift(oamHitbox, 3) + oamHitboxPointer + 2);
  local entryHitboxPointer = memory.readword(ROM, (entryPointer+6));
  if (entryHitboxPointer ~= 0)
  then
    entryHitboxPointer = bankOffset + entryHitboxPointer;
    local hitboxCount = memory.readbyte(ROM, entryHitboxPointer);

    if (hitboxCount ~= 0)
    then
      for hitboxNumber=0,(hitboxCount-1)
      do
        draw_enemy_hitbox(entryHitboxPointer, hitboxNumber, entryPointer, enemyRelativeX, enemyRelativeY)
      end;
    end;
  end;
end;
function draw_enemy_hitbox(entryHitboxPointer, hitboxNumber, entryPointer, enemyRelativeX, enemyRelativeY)
  local hitboxOffset = (entryHitboxPointer + 2 + multiply_by_twelve(hitboxNumber));

  local entryLeft   = memory.readword(ROM, (hitboxOffset + 0));
  local entryTop    = memory.readword(ROM, (hitboxOffset + 2));
  local entryRight  = memory.readword(ROM, (hitboxOffset + 4));
  local entryBottom = memory.readword(ROM, (hitboxOffset + 6));

  local entryOffsetX = memory.readword(ROM, entryPointer + 0);
  local entryOffsetY = memory.readword(ROM, entryPointer + 2);

  local hitboxLeft   = bit.lshift(bit.band((enemyRelativeX + entryOffsetX + entryLeft), 0xFFFF), 1);
  local hitboxTop    = bit.lshift(bit.band((enemyRelativeY + entryOffsetY + entryTop ), 0xFFFF), 1);
  local hitboxWidth  = bit.lshift(bit.band((entryRight - entryLeft), 0xFFFF), 1);
  local hitboxHeight = bit.lshift(bit.band((entryBottom - entryTop), 0xFFFF), 1);

  gui.rectangle(hitboxLeft, hitboxTop, hitboxWidth, hitboxHeight, 1, COLOR_ENEMY, -1);
end;

function draw_enemy_data(enemyNumber, enemyOffset, enemyId, enemyLeft, enemyTop)
  local enemyData = string.format("%2d: %04X", enemyNumber, enemyId);
  gui.text(SCREEN_X_ENEMY_SUMMARY, multiply_by_twelve(enemyNumber), enemyData, COLOR_ENEMY);

  local enemyHealth = memory.readword(WRAM, 0x0F8C + enemyOffset);
  local enemySpawnHealth = memory.readword(ROM, (BANK_OFFSET_A0 + (enemyId + 4)));
  if (enemySpawnHealth ~= 0) and (enemyHealth ~= 0)
  then
    local enemyHealthData = enemyHealth .. "/" .. enemySpawnHealth;
    gui.text(enemyLeft, enemyTop, enemyData, COLOR_ENEMY);
    gui.text(enemyLeft, (enemyTop - 0x20), enemyHealthData, COLOR_ENEMY);

    local enemyHealthPercent = math.floor((ENEMY_HEALTH_BAR_WIDTH * enemyHealth) / enemySpawnHealth);

    gui.solidrectangle(enemyLeft, (enemyTop - 0x10), enemyHealthPercent, ENEMY_HEALTH_BAR_HEIGHT, COLOR_ENEMY_HEALTH_BAR);
    gui.rectangle(enemyLeft, (enemyTop - 0x10), ENEMY_HEALTH_BAR_WIDTH, ENEMY_HEALTH_BAR_HEIGHT, 1, COLOR_ENEMY, -1);
  end;
end;
function multiply_by_twelve(input)
  return (bit.lshift(input, 3) + bit.lshift(input, 2));
end;

function draw_samus(cameraX, cameraY)
  draw_samus_projectiles(cameraX, cameraY);
  draw_samus_bombs(cameraX, cameraY);
  draw_samus_hitbox();
end;

function draw_samus_projectiles(cameraX, cameraY)
  for projectileOffset=0,8,2
  do
    draw_samus_projectile(projectileOffset, cameraX, cameraY)
  end;
end;
function draw_samus_projectile(projectileOffset, cameraX, cameraY)
  local projectileDamage = memory.readword(WRAM, (0x0C2C + projectileOffset));
  if (projectileDamage > 8)
  then
    local projectileX = memory.readword(WRAM, (0x0B64 + projectileOffset));
    local projectileY = memory.readword(WRAM, (0x0B78 + projectileOffset));

    local projectileRadiusX = memory.readword(WRAM, (0x0BB4 + projectileOffset));
    local projectileRadiusY = memory.readword(WRAM, (0x0BC8 + projectileOffset));

    local projectileLeft   = bit.lshift((projectileX - cameraX - projectileRadiusX), 1);
    local projectileTop    = bit.lshift((projectileY - cameraY - projectileRadiusY), 1);
    local projectileWidth  = bit.lshift(projectileRadiusX, 2);
    local projectileHeight = bit.lshift(projectileRadiusY, 2);

    gui.text(projectileLeft, (projectileTop - 16), projectileDamage, COLOR_SAMUS_PROJECTILES);
    gui.rectangle(projectileLeft, projectileTop, projectileWidth, projectileHeight, 1, COLOR_SAMUS_PROJECTILES, -1);
  end;
end;

function draw_samus_bombs(cameraX, cameraY)
  for bombOffset=0,8,2
  do
    draw_samus_bomb(bombOffset, cameraX, cameraY)
  end;
end;
function draw_samus_bomb(bombOffset, cameraX, cameraY)
  local bombTimer = memory.readword(WRAM, (0x0C86 + bombOffset));
  if (bombTimer ~= 0)
  then
    local bombX = memory.readword(WRAM, (0x0B6E + bombOffset));
    local bombY = memory.readword(WRAM, (0x0B82 + bombOffset));

    local bombRadiusX = memory.readword(WRAM, (0x0BBE + bombOffset));
    local bombRadiusY = memory.readword(WRAM, (0x0BD2 + bombOffset));

    local bombLeft   = bit.lshift((bombX - cameraX - bombRadiusX), 1);
    local bombTop    = bit.lshift((bombY - cameraY - bombRadiusY), 1);
    local bombWidth  = bit.lshift(bombRadiusX, 2);
    local bombHeight = bit.lshift(bombRadiusY, 2);

    local bombDamage = memory.readword(WRAM, (0x0C36 + bombOffset));
    gui.text(bombLeft, (bombTop - 32), bombDamage, COLOR_SAMUS_BOMBS);
    gui.text(bombLeft, (bombTop - 16), bombTimer, COLOR_SAMUS_BOMBS);
    gui.rectangle(bombLeft, bombTop, bombWidth, bombHeight, 1, COLOR_SAMUS_BOMBS, -1);
  end;
end;

function draw_samus_hitbox()
  local samusRadiusX = memory.readword(WRAM, 0x0AFE);
  local samusRadiusY = memory.readword(WRAM, 0x0B00);

  local samusLeft  = bit.lshift((SCREEN_X_HALF - samusRadiusX), 1);
  local samusRight = bit.lshift((SCREEN_X_HALF + samusRadiusX), 1);
  local samusTop   = bit.lshift((SCREEN_Y_HALF - samusRadiusY), 1);

  local samusWidth  = bit.lshift(samusRadiusX, 2);
  local samusHeight = bit.lshift(samusRadiusY, 2);

  draw_samus_data(samusRight);

  local samusHitboxColor = calculate_samus_hitbox_color();
  gui.rectangle(samusLeft, samusTop, samusWidth, samusHeight, 1, samusHitboxColor, -1);
end;
function calculate_samus_hitbox_color()
  local actualDistance = 0x00000000;
  if (SAMUS_X_NEXT < SAMUS_X_LAST)
  then
    actualDistance = (SAMUS_X_LAST - SAMUS_X_NEXT);
  else
    actualDistance = (SAMUS_X_NEXT - SAMUS_X_LAST);
  end;
  local expectedDistance = (bit.lshift((memory.readword(WRAM, 0x0B42) + memory.readword(WRAM, 0x0B46)), 0x10) + (memory.readword(WRAM, 0x0B44) + memory.readword(WRAM, 0x0B48)));

  if (actualDistance > expectedDistance) -- arm pumping
  then
    return COLOR_SAMUS_HITBOX_FASTEST;
  elseif (actualDistance == expectedDistance)
  then
    return COLOR_SAMUS_HITBOX_FASTER;
  else
    local actualDistanceQuadrupled = bit.lshift(actualDistance, 2);
    local expectedDistanceDoubled = (bit.lshift(expectedDistance, 1));

    if (actualDistanceQuadrupled > (expectedDistanceDoubled + expectedDistance))
    then
      return COLOR_SAMUS_HITBOX_AVERAGE;
    elseif (actualDistanceQuadrupled > expectedDistanceDoubled)
    then
      return COLOR_SAMUS_HITBOX_SLOWER;
    else
      return COLOR_SAMUS_HITBOX_SLOWEST;
    end;
  end;
end;

function draw_samus_data(samusRight)
  local cooldown = memory.readword(WRAM, 0x0CCC);
  if (cooldown ~= 0)
  then
    gui.text(samusRight, bit.lshift((SCREEN_Y_HALF - 16)), cooldown, 0x00FF00);
  end;

  local charge = memory.readword(WRAM, 0x0CD0);
  if (charge ~= 0)
  then;
    gui.text(samusRight, bit.lshift((SCREEN_Y_HALF - 8)), charge, 0x00FF00);
  end;

  local recoil = memory.readword(WRAM, 0x18AA);
  if (recoil ~= 0)
  then
    gui.text(samusRight, bit.lshift((SCREEN_Y_HALF)), recoil, 0x00FFFF)
  else
    local invincibility = memory.readword(WRAM, 0x18A8);
    if (invincibility ~= 0)
    then
      gui.text(samusRight, bit.lshift((SCREEN_Y_HALF)), invincibility, 0x00FFFF)
    end;
  end;

  local shine = memory.readbyte(WRAM, 0x0A68);
  if (shine ~= 0)
  then
    gui.text(samusRight, bit.lshift((SCREEN_Y_HALF + 8)), shine, 0x00FFFF);
  end;
end;