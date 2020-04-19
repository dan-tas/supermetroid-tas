--[[
reset-lua
run-lua src\test\resources\rng-logger\lsnes-rng-logger.lua
]]

-- TASFlag = 0;
DebugFlag = 0;

function flip(pixel, flip)
  if flip == 0
  then
    return pixel;
  else
    return 0x1F - pixel;
  end;
end;
function draw_tile_boundaries(TileX, TileY, color)
  gui.line(TileX+     0x1F        , TileY+     0x1F        , TileX+     0x00        , TileY+     0x1F        , color);
  gui.line(TileX+     0x00        , TileY+     0x1F        , TileX+     0x00        , TileY+     0x00        , color);
  gui.line(TileX+     0x00        , TileY+     0x00        , TileX+     0x1F        , TileY+     0x00        , color);
  gui.line(TileX+     0x1F        , TileY+     0x00        , TileX+     0x1F        , TileY+     0x1F        , color);
end;
function default_slope(TileX, TileY, VFlip)
  draw_tile_boundaries(TileX, TileY, slope_color);

  gui.line(TileX+     0x00        , TileY+flip(0x04, VFlip), TileX+     0x1F        , TileY+flip(0x04, VFlip), slope_color);
end;

slope_color = 0x00FF00;
slope = {
    [0x00] = function () -- 1/2 tile rectangle, 100% wide 50% tall, covers bottom by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x00, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+     0x00        , TileY+flip(0x10, VFlip), TileX+     0x1F        , TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), slope_color);
    end,
    [0x01] = function () -- 1/2 tile rectangle, 50% wide 100% tall, covers right by default
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+     0x1F        , TileX+flip(0x10, HFlip), TileY+     0x00        , slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x02] = function () -- 1/4 tile square, 50% wide 50% tall, covers bottom right by default
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), slope_color);
    end,
    [0x03] = function () -- 3/4 tile anti-square, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x00, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x04] = function () -- unused?
      draw_tile_boundaries(TileX, TileY, slope_color);
    end,
    [0x05] = function () -- 1/4 tile triangle, 100% wide 50% tall, covers bottom by default, horizontally symmetric
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+     0x00        , TileY+flip(0x1F, VFlip), TileX+     0x0F        , TileY+flip(0x10, VFlip), slope_color); -- this line becomes the 3rd line when HFlip is 1
      gui.line(TileX+     0x10        , TileY+flip(0x10, VFlip), TileX+     0x1F        , TileY+flip(0x1F, VFlip), slope_color); -- this line becomes the 2nd line when HFlip is 1
    end,
    [0x06] = function () -- 1/2 tile triangle, covers bottom by default, 100% wide 50% tall, covers bottom by default, horizontally symmetric
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+     0x00        , TileY+flip(0x1F, VFlip), TileX+     0x0F        , TileY+flip(0x00, VFlip), slope_color); -- this line becomes the 3rd line when HFlip is 1
      gui.line(TileX+     0x10        , TileY+flip(0x00, VFlip), TileX+     0x1F        , TileY+flip(0x1F, VFlip), slope_color); -- this line becomes the 2nd line when HFlip is 1
    end,
    [0x07] = function () -- 1/2 tile unmagnet, pushes up by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x00, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+     0x00        , TileY+flip(0x10, VFlip), TileX+     0x1F        , TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), slope_color);
    
      gui.line(TileX+     0x00        , TileY+flip(0x14, VFlip), TileX+     0x1F        , TileY+flip(0x14, VFlip), slope_color);
    end,
    [0x08] = function () -- is this used?
      default_slope(TileX, TileY, VFlip);
    end,    
    [0x09] = function () -- is this used?
      default_slope(TileX, TileY, VFlip);
    end,    
    [0x0A] = function () -- is this used?
      default_slope(TileX, TileY, VFlip);
    end,    
    [0x0B] = function () -- is this used?
      default_slope(TileX, TileY, VFlip);
    end,    
    [0x0C] = function () -- is this used?
      default_slope(TileX, TileY, VFlip);
    end,    
    [0x0D] = function () -- is this used?
      default_slope(TileX, TileY, VFlip);
    end,    
    [0x0E] = function () -- four step bars, solid for (4+3+2+1)/(4+4+4+4), covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+     0x00        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x18, VFlip), slope_color); -- this line becomes the 4th line when HFlip is 1
      gui.line(TileX+     0x00        , TileY+flip(0x18, VFlip), TileX+     0x1F        , TileY+flip(0x18, VFlip), slope_color);
      gui.line(TileX+     0x1F        , TileY+flip(0x18, VFlip), TileX+     0x1F        , TileY+flip(0x1F, VFlip), slope_color); -- this line becomes the 2nd line when HFlip is 1
    
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x17, VFlip), TileX+flip(0x08, HFlip), TileY+flip(0x17, VFlip), slope_color);
      gui.line(TileX+flip(0x08, HFlip), TileY+flip(0x17, VFlip), TileX+flip(0x08, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x08, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x17, VFlip), slope_color);

      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x0F, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x0F, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x0F, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x08, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x08, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x08, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x08, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x0F, VFlip), slope_color);

      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x07, VFlip), TileX+flip(0x18, HFlip), TileY+flip(0x07, VFlip), slope_color);
      gui.line(TileX+flip(0x18, HFlip), TileY+flip(0x07, VFlip), TileX+flip(0x18, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x18, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x07, VFlip), slope_color);
    end,
    [0x0F] = function () -- tourian escape room 3 stairs
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x0F, HFlip), TileY+flip(0x18, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x17, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x10] = function () -- four horizontal lines, evenly spaced, unused?
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+     0x1F        , TileY+flip(0x17, VFlip), TileX+     0x00        , TileY+flip(0x17, VFlip), slope_color);
      gui.line(TileX+     0x1F        , TileY+flip(0x0F, VFlip), TileX+     0x00        , TileY+flip(0x0F, VFlip), slope_color);
      gui.line(TileX+     0x1F        , TileY+flip(0x07, VFlip), TileX+     0x00        , TileY+flip(0x07, VFlip), slope_color);
    end,
    [0x11] = function () -- four vertical lines, evenly spaced, unused?
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
      gui.line(TileX+flip(0x17, HFlip), TileY+     0x00        , TileX+flip(0x17, HFlip), TileY+     0x1F        , slope_color);
      gui.line(TileX+flip(0x0F, HFlip), TileY+     0x00        , TileX+flip(0x0F, HFlip), TileY+     0x1F        , slope_color);
      gui.line(TileX+flip(0x07, HFlip), TileY+     0x00        , TileX+flip(0x07, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x12] = function () -- 1x1 diagonal, 1/2 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x13] = function () -- full tile unmagnet, pushes up by default
      default_slope(TileX, TileY, VFlip);
    end,
    [0x14] = function () -- 1x1 diagonal, 1/4 solid, covers bottom right by default
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), slope_color);
    end,
    [0x15] = function () -- 1x1 diagonal, 3/4 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x00, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x0F, VFlip), TileX+flip(0x0F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x16] = function () -- 2x1 diagonal, wider than it is tall, 1/4 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x10, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), slope_color);
    end,
    [0x17] = function () -- 2x1 diagonal, wider than it is tall, 3/4 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x00, HFlip), TileY+flip(0x10, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x0F, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x18] = function () -- 3x1 diagonal, wider than it is tall, 1/6 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x15, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x15, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), slope_color);
    end,
    [0x19] = function () -- 3x1 diagonal, wider than it is tall, 3/6 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x00, HFlip), TileY+flip(0x15, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x15, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x0A, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x0A, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), slope_color);
    end,
    [0x1A] = function () -- 3x1 diagonal, wider than it is tall, 5/6 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x00, HFlip), TileY+flip(0x0A, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x0A, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x1B] = function () -- 1x2 diagonal, taller than it is wide, 1/4 solid, covers bottom right by default
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x10, HFlip), TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x1C] = function () -- 1x2 diagonal, taller than it is wide, 3/4 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x0F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x10, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x1D] = function () -- 1x3 diagonal, taller than it is wide, 1/6 solid, covers bottom right by default
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x15, HFlip), TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x15, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x1E] = function () -- 1x3 diagonal, taller than it is wide, 3/6 solid, covers bottom right by default
      gui.line(TileX+flip(0x1F, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x0A, HFlip), TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x0A, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x15, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x15, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end,
    [0x1F] = function () -- 1x3 diagonal, taller than it is wide, 5/6 solid, covers bottom right by default
      gui.line(TileX+     0x1F        , TileY+flip(0x1F, VFlip), TileX+     0x00        , TileY+flip(0x1F, VFlip), slope_color);
      gui.line(TileX+flip(0x00, HFlip), TileY+flip(0x1F, VFlip), TileX+flip(0x0A, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x0A, HFlip), TileY+flip(0x00, VFlip), TileX+flip(0x1F, HFlip), TileY+flip(0x00, VFlip), slope_color);
      gui.line(TileX+flip(0x1F, HFlip), TileY+     0x00        , TileX+flip(0x1F, HFlip), TileY+     0x1F        , slope_color);
    end
}

-- doors = {[0x88FE]=true, [0x890A]=true, [0x8916]=true, [0x8922]=true, [0x892E]=true, [0x893A]=true, [0x8946]=true, [0x8952]=true, [0x895E]=true, [0x896A]=true, [0x8976]=true, [0x8982]=true, [0x898E]=true, [0x899A]=true, [0x89A6]=true, [0x89B2]=true, [0x89BE]=true, [0x89CA]=true, [0x89D6]=true, [0x89E2]=true, [0x89EE]=true, [0x89FA]=true, [0x8A06]=true, [0x8A12]=true, [0x8A1E]=true, [0x8A2A]=true, [0x8A36]=true, [0x8A42]=true, [0x8A4E]=true, [0x8A5A]=true, [0x8A66]=true, [0x8A72]=true, [0x8A7E]=true, [0x8A8A]=true, [0x8A96]=true, [0x8AA2]=true, [0x8AAE]=true, [0x8ABA]=true, [0x8AC6]=true, [0x8AD2]=true, [0x8ADE]=true, [0x8AEA]=true, [0x8AF6]=true, [0x8B02]=true, [0x8B0E]=true, [0x8B1A]=true, [0x8B26]=true, [0x8B32]=true, [0x8B3E]=true, [0x8B4A]=true, [0x8B56]=true, [0x8B62]=true, [0x8B6E]=true, [0x8B7A]=true, [0x8B86]=true, [0x8B92]=true, [0x8B9E]=true, [0x8BAA]=true, [0x8BB6]=true, [0x8BC2]=true, [0x8BCE]=true, [0x8BDA]=true, [0x8BE6]=true, [0x8BF2]=true, [0x8BFE]=true, [0x8C0A]=true, [0x8C16]=true, [0x8C22]=true, [0x8C2E]=true, [0x8C3A]=true, [0x8C46]=true, [0x8C52]=true, [0x8C5E]=true, [0x8C6A]=true, [0x8C76]=true, [0x8C82]=true, [0x8C8E]=true, [0x8C9A]=true, [0x8CA6]=true, [0x8CB2]=true, [0x8CBE]=true, [0x8CCA]=true, [0x8CD6]=true, [0x8CE2]=true, [0x8CEE]=true, [0x8CFA]=true, [0x8D06]=true, [0x8D12]=true, [0x8D1E]=true, [0x8D2A]=true, [0x8D36]=true, [0x8D42]=true, [0x8D4E]=true, [0x8D5A]=true, [0x8D66]=true, [0x8D72]=true, [0x8D7E]=true, [0x8D8A]=true, [0x8D96]=true, [0x8DA2]=true, [0x8DAE]=true, [0x8DBA]=true, [0x8DC6]=true, [0x8DD2]=true, [0x8DDE]=true, [0x8DEA]=true, [0x8DF6]=true, [0x8E02]=true, [0x8E0E]=true, [0x8E1A]=true, [0x8E26]=true, [0x8E32]=true, [0x8E3E]=true, [0x8E4A]=true, [0x8E56]=true, [0x8E62]=true, [0x8E6E]=true, [0x8E7A]=true, [0x8E86]=true, [0x8E92]=true, [0x8E9E]=true, [0x8EAA]=true, [0x8EB6]=true, [0x8EC2]=true, [0x8ECE]=true, [0x8EDA]=true, [0x8EE6]=true, [0x8EF2]=true, [0x8EFE]=true, [0x8F0A]=true, [0x8F16]=true, [0x8F22]=true, [0x8F2E]=true, [0x8F3A]=true, [0x8F46]=true, [0x8F52]=true, [0x8F5E]=true, [0x8F6A]=true, [0x8F76]=true, [0x8F82]=true, [0x8F8E]=true, [0x8F9A]=true, [0x8FA6]=true, [0x8FB2]=true, [0x8FBE]=true, [0x8FCA]=true, [0x8FD6]=true, [0x8FE2]=true, [0x8FEE]=true, [0x8FFA]=true, [0x9006]=true, [0x9012]=true, [0x901E]=true, [0x902A]=true, [0x9036]=true, [0x9042]=true, [0x904E]=true, [0x905A]=true, [0x9066]=true, [0x9072]=true, [0x907E]=true, [0x908A]=true, [0x9096]=true, [0x90A2]=true, [0x90AE]=true, [0x90BA]=true, [0x90C6]=true, [0x90D2]=true, [0x90DE]=true, [0x90EA]=true, [0x90F6]=true, [0x9102]=true, [0x910E]=true, [0x911A]=true, [0x9126]=true, [0x9132]=true, [0x913E]=true, [0x914A]=true, [0x9156]=true, [0x9162]=true, [0x916E]=true, [0x917A]=true, [0x9186]=true, [0x9192]=true, [0x919E]=true, [0x91AA]=true, [0x91B6]=true, [0x91C2]=true, [0x91CE]=true, [0x91DA]=true, [0x91E6]=true, [0x91F2]=true, [0x91FE]=true, [0x920A]=true, [0x9216]=true, [0x9222]=true, [0x922E]=true, [0x923A]=true, [0x9246]=true, [0x9252]=true, [0x925E]=true, [0x926A]=true, [0x9276]=true, [0x9282]=true, [0x928E]=true, [0x929A]=true, [0x92A6]=true, [0x92B2]=true, [0x92BE]=true, [0x92CA]=true, [0x92D6]=true, [0x92E2]=true, [0x92EE]=true, [0x92FA]=true, [0x9306]=true, [0x9312]=true, [0x931E]=true, [0x932A]=true, [0x9336]=true, [0x9342]=true, [0x934E]=true, [0x935A]=true, [0x9366]=true, [0x9372]=true, [0x937E]=true, [0x938A]=true, [0x9396]=true, [0x93A2]=true, [0x93AE]=true, [0x93BA]=true, [0x93C6]=true, [0x93D2]=true, [0x93DE]=true, [0x93EA]=true, [0x93F6]=true, [0x9402]=true, [0x940E]=true, [0x941A]=true, [0x9426]=true, [0x9432]=true, [0x943E]=true, [0x944A]=true, [0x9456]=true, [0x9462]=true, [0x946E]=true, [0x947A]=true, [0x9486]=true, [0x9492]=true, [0x949E]=true, [0x94AA]=true, [0x94B6]=true, [0x94C2]=true, [0x94CE]=true, [0x94DA]=true, [0x94E6]=true, [0x94F2]=true, [0x94FE]=true, [0x950A]=true, [0x9516]=true, [0x9522]=true, [0x952E]=true, [0x953A]=true, [0x9546]=true, [0x9552]=true, [0x955E]=true, [0x956A]=true, [0x9576]=true, [0x9582]=true, [0x958E]=true, [0x959A]=true, [0x95A6]=true, [0x95B2]=true, [0x95BE]=true, [0x95CA]=true, [0x95D6]=true, [0x95E2]=true, [0x95EE]=true, [0x95FA]=true, [0x9606]=true, [0x9612]=true, [0x961E]=true, [0x962A]=true, [0x9636]=true, [0x9642]=true, [0x964E]=true, [0x965A]=true, [0x9666]=true, [0x9672]=true, [0x967E]=true, [0x968A]=true, [0x9696]=true, [0x96A2]=true, [0x96AE]=true, [0x96BA]=true, [0x96C6]=true, [0x96D2]=true, [0x96DE]=true, [0x96EA]=true, [0x96F6]=true, [0x9702]=true, [0x970E]=true, [0x971A]=true, [0x9726]=true, [0x9732]=true, [0x973E]=true, [0x974A]=true, [0x9756]=true, [0x9762]=true, [0x976E]=true, [0x977A]=true, [0x9786]=true, [0x9792]=true, [0x979E]=true, [0x97AA]=true, [0x97B6]=true, [0x97C2]=true, [0x97CE]=true, [0x97DA]=true, [0x97E6]=true, [0x97F2]=true, [0x97FE]=true, [0x980A]=true, [0x9816]=true, [0x9822]=true, [0x982E]=true, [0x983A]=true, [0x9846]=true, [0x9852]=true, [0x985E]=true, [0x986A]=true, [0x9876]=true, [0x9882]=true, [0x988E]=true, [0x989A]=true, [0x98A6]=true, [0x98B2]=true, [0x98BE]=true, [0x98CA]=true, [0x98D6]=true, [0x98E2]=true, [0x98EE]=true, [0x98FA]=true, [0x9906]=true, [0x9912]=true, [0x991E]=true, [0x992A]=true, [0x9936]=true, [0x9942]=true, [0x994E]=true, [0x995A]=true, [0x9966]=true, [0x9972]=true, [0x997E]=true, [0x998A]=true, [0x9996]=true, [0x99A2]=true, [0x99AE]=true, [0x99BA]=true, [0x99C6]=true, [0x99D2]=true, [0x99DE]=true, [0x99EA]=true, [0x99F6]=true, [0x9A02]=true, [0x9A0E]=true, [0x9A1A]=true, [0x9A26]=true, [0x9A32]=true, [0x9A3E]=true, [0x9A4A]=true, [0x9A56]=true, [0x9A62]=true, [0x9A6E]=true, [0x9A7A]=true, [0x9A86]=true, [0x9A92]=true, [0x9A9E]=true, [0x9AAA]=true, [0x9AB6]=true, [0xA18C]=true, [0xA198]=true, [0xA1A4]=true, [0xA1B0]=true, [0xA1BC]=true, [0xA1C8]=true, [0xA1D4]=true, [0xA1E0]=true, [0xA1EC]=true, [0xA1F8]=true, [0xA204]=true, [0xA210]=true, [0xA21C]=true, [0xA228]=true, [0xA234]=true, [0xA240]=true, [0xA24C]=true, [0xA258]=true, [0xA264]=true, [0xA270]=true, [0xA27C]=true, [0xA288]=true, [0xA294]=true, [0xA2A0]=true, [0xA2AC]=true, [0xA2B8]=true, [0xA2C4]=true, [0xA2D0]=true, [0xA2DC]=true, [0xA2E8]=true, [0xA2F4]=true, [0xA300]=true, [0xA30C]=true, [0xA318]=true, [0xA324]=true, [0xA330]=true, [0xA33C]=true, [0xA348]=true, [0xA354]=true, [0xA360]=true, [0xA36C]=true, [0xA378]=true, [0xA384]=true, [0xA390]=true, [0xA39C]=true, [0xA3A8]=true, [0xA3B4]=true, [0xA3C0]=true, [0xA3CC]=true, [0xA3D8]=true, [0xA3E4]=true, [0xA3F0]=true, [0xA3FC]=true, [0xA408]=true, [0xA414]=true, [0xA420]=true, [0xA42C]=true, [0xA438]=true, [0xA444]=true, [0xA450]=true, [0xA45C]=true, [0xA468]=true, [0xA474]=true, [0xA480]=true, [0xA48C]=true, [0xA498]=true, [0xA4A4]=true, [0xA4B0]=true, [0xA4BC]=true, [0xA4C8]=true, [0xA4D4]=true, [0xA4E0]=true, [0xA4EC]=true, [0xA4F8]=true, [0xA504]=true, [0xA510]=true, [0xA51C]=true, [0xA528]=true, [0xA534]=true, [0xA540]=true, [0xA54C]=true, [0xA558]=true, [0xA564]=true, [0xA570]=true, [0xA57C]=true, [0xA588]=true, [0xA594]=true, [0xA5A0]=true, [0xA5AC]=true, [0xA5B8]=true, [0xA5C4]=true, [0xA5D0]=true, [0xA5DC]=true, [0xA5E8]=true, [0xA5F4]=true, [0xA600]=true, [0xA60C]=true, [0xA618]=true, [0xA624]=true, [0xA630]=true, [0xA63C]=true, [0xA648]=true, [0xA654]=true, [0xA660]=true, [0xA66C]=true, [0xA678]=true, [0xA684]=true, [0xA690]=true, [0xA69C]=true, [0xA6A8]=true, [0xA6B4]=true, [0xA6C0]=true, [0xA6CC]=true, [0xA6D8]=true, [0xA6E4]=true, [0xA6F0]=true, [0xA6FC]=true, [0xA708]=true, [0xA714]=true, [0xA720]=true, [0xA72C]=true, [0xA738]=true, [0xA744]=true, [0xA750]=true, [0xA75C]=true, [0xA768]=true, [0xA774]=true, [0xA780]=true, [0xA78C]=true, [0xA798]=true, [0xA7A4]=true, [0xA7B0]=true, [0xA7BC]=true, [0xA7C8]=true, [0xA7D4]=true, [0xA7E0]=true, [0xA7EC]=true, [0xA7F8]=true, [0xA810]=true, [0xA828]=true, [0xA834]=true, [0xA840]=true, [0xA84C]=true, [0xA858]=true, [0xA864]=true, [0xA870]=true, [0xA87C]=true, [0xA888]=true, [0xA894]=true, [0xA8A0]=true, [0xA8AC]=true, [0xA8B8]=true, [0xA8C4]=true, [0xA8D0]=true, [0xA8DC]=true, [0xA8E8]=true, [0xA8F4]=true, [0xA900]=true, [0xA90C]=true, [0xA918]=true, [0xA924]=true, [0xA930]=true, [0xA93C]=true, [0xA948]=true, [0xA954]=true, [0xA960]=true, [0xA96C]=true, [0xA978]=true, [0xA984]=true, [0xA990]=true, [0xA99C]=true, [0xA9A8]=true, [0xA9B4]=true, [0xA9C0]=true, [0xA9CC]=true, [0xA9D8]=true, [0xA9E4]=true, [0xA9F0]=true, [0xA9FC]=true, [0xAA08]=true, [0xAA14]=true, [0xAA20]=true, [0xAA2C]=true, [0xAA38]=true, [0xAA44]=true, [0xAA50]=true, [0xAA5C]=true, [0xAA68]=true, [0xAA74]=true, [0xAA80]=true, [0xAA8C]=true, [0xAA98]=true, [0xAAA4]=true, [0xAAB0]=true, [0xAABC]=true, [0xAAC8]=true, [0xAAD4]=true, [0xAAE0]=true, [0xAAEC]=true, [0xAAF8]=true, [0xAB04]=true, [0xAB10]=true, [0xAB1C]=true, [0xAB28]=true, [0xAB34]=true, [0xAB40]=true, [0xAB4C]=true, [0xAB58]=true, [0xAB64]=true, [0xAB70]=true, [0xAB7C]=true, [0xAB88]=true, [0xAB94]=true, [0xABA0]=true, [0xABAC]=true, [0xABB8]=true, [0xABC4]=true, [0xABCF]=true, [0xABDA]=true, [0xABE5]=true}
outline = {
    [0x00] = function () -- Air
    end,
    [0x01] = function () -- Slope
      local b = memory.readbyte('BUS', BTS);
      if bit.band(b, 0x40) ~= 0 then
        HFlip = 1;
      else
        HFlip = 0;
      end;
        
      if bit.band(b, 0x80) ~= 0 then
        VFlip = 1;
      else
        VFlip = 0;
      end;

      local sf = bit.band(b, 0x1F);
      slope[sf]();
    end,
    [0x02] = function () -- Air, tricks X-ray
    end,
    [0x03] = function () -- Treadmill
      draw_tile_boundaries(TileX, TileY, 0x80FF40);
    end,
    [0x04] = function () -- unused?
    end,
    [0x05] = function () -- Horizontal extend
      stack = stack + 1;
      local b = memory.readsbyte('BUS', BTS);
      if stack < 16 and (b ~= 0) then
        BTS = BTS + b;
        Clip = Clip + bit.lrshift(b, -1);
        outline[bit.lrshift(memory.readword('BUS', Clip), 12)]();
      else
        draw_tile_boundaries(TileX, TileY, 0xFF00FF);
      end;
    end,
    [0x06] = function () -- unused?
    end,
    [0x07] = function () -- unused?
    end,    
    [0x08] = function () -- Normal block
      draw_tile_boundaries(TileX, TileY, 0xFF0000);
    end,
    [0x09] = function () -- Transition block
      door = memory.readword('BUS', 0x8F0000 + memory.readword('BUS', 0x7E07B5) + 2*bit.band(memory.readbyte('BUS', BTS), 0x7F))
      transition_color = 0x00FFFF;
      draw_tile_boundaries(TileX, TileY, transition_color);
--[[      
      if door == 0x8000 then
        draw_tile_boundaries(TileX, TileY, transition_color);
      elseif doors[door] then
        draw_tile_boundaries(TileX, TileY, transition_color);
      else
        draw_tile_boundaries(TileX, TileY, transition_color);
      end
]]--      
    end,
    [0x0A] = function () -- Spike block
      draw_tile_boundaries(TileX, TileY, 0x0000FF);
    end,
    [0x0B] = function () -- Crumble block
      draw_tile_boundaries(TileX, TileY, 0x0000FF);
    end,
    [0x0C] = function () -- Shot block
      local b = memory.readbyte('BUS', BTS)
      if b >= 0x40 and b <= 0x43 then
        draw_tile_boundaries(TileX, TileY, 0xFFA500); -- makes doors orange
      else
        draw_tile_boundaries(TileX, TileY, 0x0000FF);
      end;
    end,
    [0x0D] = function ()
      stack = stack + 1
      local b = memory.readsbyte('BUS', BTS)
      if stack < 16 and (b ~= 0) then
        BTS = BTS + b*width
        Clip = Clip + bit.lrshift(b*width, -1)
        outline[bit.lrshift(memory.readword('BUS', Clip), 12)]();
      else
        draw_tile_boundaries(TileX, TileY, 0xFF00FF);
      end
    end, -- Vertical extend
    [0x0E] = function ()
      draw_tile_boundaries(TileX, TileY, 0x0000FF);
    end, -- Grapple block
    [0x0F] = function ()
      draw_tile_boundaries(TileX, TileY, 0x0000FF);
    end -- Bomb block
}

function on_paint()
  --[[
    lsnes crashes if you attempt to draw the enemy hitboxes on the first couple 
    frames when there is junk data. Therefore, do not draw anything until later
  ]]--
  if (movie.currentframe() >= 8500) then
    cur_gamemode = memory.readbyte('BUS', 0x7E0998)
    if cur_gamemode ~= 0x00 then
      local cameraX, cameraY = bit.band(memory.readword('BUS', 0x7E0AF6)-0x80, 0xFFFF), bit.band(memory.readword('BUS', 0x7E0AFA)-0x70, 0xFFFF)
      -- these are the co-ordinates of the top-left of the screen
      if cameraX >= 10000 then
        cameraX = cameraX-0xFFFF;
      end;
      if cameraY >= 10000 then
        cameraY = cameraY-0xFFFF;
      end;
      
	    draw_room_tiles(cameraX, cameraY);
      draw_item_drops_and_enemies(cameraX, cameraY);
      draw_samus_and_projectiles(cameraX, cameraY);
    end;
  end;
end;

function draw_room_tiles(cameraX, cameraY)
  width = memory.readword('BUS', 0x7E07A5);
  -- this is how wide the room is in blocks
  for y=0,14 do
    for x=0,16 do
      stack = 0;
      -- for when garbage data causes extend blocks that make infinite loops
      TileX, TileY = x*32 - (bit.band(cameraX, 0x000F)*2), y*32 - (bit.band(cameraY, 0x000F)*2);
      -- this if for pixel-aligning the grid, because the screen doesn't just scroll per block!
      a = bit.lrshift(bit.band(cameraX+x*16, 0xFFFF), 4) + bit.band(bit.lrshift(bit.band(cameraY+y*16, 0xFFF), 4)*width, 0xFFFF);
      -- get block's tile number
      BTS = 0x7F0000 + ((0x6402 + a)%0x10000);
      -- BTS of block
      Clip = 0x7F0000 + ((0x0002 + a*2)%0x10000);

      -- clipdata of block
      local blockType = bit.lrshift(memory.readword('BUS', Clip), 12);
      if DebugFlag ~= 0 or blockType == 0x09 or blockType == 0x02 then
        gui.text(TileX+8, TileY+1, string.format("%02X", blockType) , 0xFF0000);
        gui.text(TileX+8, TileY+12, string.format("%02X",memory.readbyte('BUS', BTS)), 0xFFA500);
      end
      outline[blockType]();
      -- that's for the block's clipdata nibble
    end
  end
  
  gui.text(0, 0, string.format("cameraX: %03X\ncameraY: %03X\nClip: %X\n", bit.lrshift(bit.band(cameraX, 0xFFF), 4), bit.lrshift(bit.band(cameraY, 0xFFF), 4), 0x7F0002 + (bit.lrshift(bit.band(cameraX, 0xFFF), 4) + bit.lrshift(bit.band(cameraY, 0xFFF), 4)*width)*2), 0x00FFFF);
end;

function draw_item_drops_and_enemies(cameraX, cameraY)
  for i=0,18 do
    -- draw item drops
    -- Known issue: The drop box shows the frame after the enemy dies, and the drop cannot be collected
    -- for multiple frames after that. Also, sometimes the box is much bigger than it should be
    local enabled = memory.readword('BUS', 0x7E1996 + i*2);
    if enabled ~= 0 then
      local projectileX, projectileY = memory.readword('BUS', 0x7E1A4B + i*2), memory.readword('BUS', 0x7E1A93 + i*2);
      local header = memory.readword('BUS', 0x7E1997 + i*2);
      local pradiusX = memory.readbyte('BUS', 0x860000 + header + 6);
      local pradiusY = memory.readbyte('BUS', 0x860000 + header + 7);
      
      local topleft = {projectileX - cameraX - pradiusX, projectileY - cameraY - pradiusY};
      local bottomright = {projectileX - cameraX + pradiusX, projectileY - cameraY + pradiusY};
      gui.rectangle(topleft[1]*2, topleft[2]*2, (bottomright[1]-topleft[1])*2,(bottomright[2] - topleft[2])*2, 1, 0x00FF00, -1);
    end;
  end;

  -- draw enemy hitbox
  local y = 0;
  local n_enemies = memory.readbyte("WRAM", 0x0E4E);
  if n_enemies ~= 0 then
    for j=1,n_enemies do
      local i = n_enemies - j;

      local enemyOffset = 0x40 * i;
      local enemyX = memory.readword("WRAM", 0x0F7A + enemyOffset);
      local enemyY = memory.readword("WRAM", 0x0F7E + enemyOffset);
      local eradiusX = memory.readword("WRAM", 0x0F82 + enemyOffset);
      local eradiusY = memory.readword("WRAM", 0x0F84 + enemyOffset);
      local topleft = {(enemyX - cameraX - eradiusX), (enemyY - cameraY - eradiusY)};
      local bottomright = {2 * eradiusX, 2 * eradiusY};
            
      if bit.band(memory.readbyte("WRAM", 0x0F88 + enemyOffset), 4) == 0 or memory.readbyte("WRAM", 0x0F8A + enemyOffset) == 4 then
        gui.rectangle(2 * topleft[1], 2 * topleft[2], 2 * bottomright[1], 2 * bottomright[2], 1, 0x808080, -1);
      else
        local oamHitboxPointer = memory.readword("WRAM", 0x0F8E + enemyOffset);
        if oamHitboxPointer ~= 0 then
          local bank = bit.lshift(memory.readbyte("WRAM", 0x0FA6 + enemyOffset), 0x10);
          oamHitboxPointer = bank + oamHitboxPointer;
          local n_oamHitbox = memory.readbyte("BUS", oamHitboxPointer);
          if n_oamHitbox ~= 0 then
            for ii=0,n_oamHitbox-1 do
              local entryPointer = oamHitboxPointer + 2 + ii*8;
              local entryXOffset = memory.readsword("BUS", entryPointer+0);
              local entryYOffset = memory.readsword("BUS", entryPointer+2);
              local entryHitboxPointer = memory.readword("BUS", entryPointer+6);

              if entryHitboxPointer ~= 0 then
                entryHitboxPointer = bank + entryHitboxPointer;
                local n_hitbox = memory.readbyte("BUS", entryHitboxPointer);
                if n_hitbox ~= 0 then
                  for iii=0,n_hitbox-1 do
                    local entryLeft   = memory.readsword("BUS", entryHitboxPointer + 2 + iii*12 + 0);
                    local entryTop    = memory.readsword("BUS", entryHitboxPointer + 2 + iii*12 + 2);
                    local entryRight  = memory.readsword("BUS", entryHitboxPointer + 2 + iii*12 + 4);
                    local entryBottom = memory.readsword("BUS", entryHitboxPointer + 2 + iii*12 + 6);

                    gui.rectangle(
                      2 * (enemyX - cameraX + entryXOffset + entryLeft),
                      2 * (enemyY - cameraY + entryYOffset + entryTop),
                      2 * (entryRight - entryLeft),
                      2 * (entryBottom - entryTop),
                      1, 0x808080, -1);
                  end;
                end;
              end;
            end;
          end;
        end;
      end;
      
      -- show enemy slot and ID
      gui.text(2*topleft[1], 2*topleft[2], string.format("%2d: %04X", i, memory.readword("WRAM", 0x0F78 + i*64)), 0x808080);
      gui.text(2*224, y, string.format("%2d: %04X", i, memory.readword("WRAM", 0x0F78 + i*64)), 0x808080);
      y = y + 11;
      
      -- show enemy health
      local enemyspawnhealth = memory.readword("BUS", 0xA00004 + memory.readword("WRAM", 0x0F78 + enemyOffset));
      if enemyspawnhealth ~= 0 then
        local enemyhealth = memory.readword("WRAM", 0x0F8C + enemyOffset);
        gui.text(2 * topleft[1], 2 * (topleft[2]-16), enemyhealth .. "/" .. enemyspawnhealth, 0x808080);
        
        if enemyhealth ~= 0 then -- draw enemy health bar
          gui.solidrectangle(2*topleft[1], 2*(topleft[2]-8), 2*math.floor(enemyhealth/enemyspawnhealth*32), 2*(3), 0x606060);
          gui.rectangle(2*topleft[1], 2*(topleft[2]-8), 2*(32), 2*(3), 1, 0x808080, -1);
        end;
      end;
    end;
  end;
end;

-- Make Samus' hitbox change color based on environmental friction
local SAMUS_X_LAST = 0x00000000;
local SAMUS_X_THIS = 0x00000000;
local ACTUAL_DISTANCE = 0x00000000;
local EXPECTED_DISTANCE = 0x00000000;
local SAMUS_HITBOX_COLOR = 0x80FFFF;

function on_frame() 
  SAMUS_X_LAST = (0x10000 * memory.readword("WRAM", 0x0AF6)) + memory.readword("WRAM", 0x0AF8);
end;
function on_frame_emulated() 
  SAMUS_X_LAST = SAMUS_X_THIS;
  SAMUS_X_THIS = (0x10000 * memory.readword("WRAM", 0x0AF6)) + memory.readword("WRAM", 0x0AF8);

  if (SAMUS_X_THIS < SAMUS_X_LAST) then ACTUAL_DISTANCE = (SAMUS_X_LAST - SAMUS_X_THIS); else ACTUAL_DISTANCE = (SAMUS_X_THIS - SAMUS_X_LAST); end;
  EXPECTED_DISTANCE = (0x10000 * (memory.readword("WRAM", 0x0B42) + memory.readword("WRAM", 0x0B46))) + (memory.readword("WRAM", 0x0B44) + memory.readword("WRAM", 0x0B48));
  
  if (ACTUAL_DISTANCE > EXPECTED_DISTANCE) -- arm pumping
  then
    SAMUS_HITBOX_COLOR = 0x00FF00;
  elseif (ACTUAL_DISTANCE == EXPECTED_DISTANCE)
  then
    SAMUS_HITBOX_COLOR = 0x80FFFF;
  elseif ((4 * ACTUAL_DISTANCE) > (3 * EXPECTED_DISTANCE))
  then
    SAMUS_HITBOX_COLOR = 0xFF8000;
  elseif ((4 * ACTUAL_DISTANCE) > (2 * EXPECTED_DISTANCE))
  then
    SAMUS_HITBOX_COLOR = 0xFF0000;
  else
    SAMUS_HITBOX_COLOR = 0x800000;
  end;
end;
function draw_samus_and_projectiles(cameraX, cameraY)
  for i=0,9 do
    -- draw projectile hitbox
    local projectileX, projectileY = memory.readword('BUS', 0x7E0B64 + i*2), memory.readword('BUS', 0x7E0B78 + i*2)
    local pradiusX, pradiusY = memory.readsword('BUS', 0x7E0BB4 + i*2), memory.readsword('BUS', 0x7E0BC8 + i*2)
    local topleft = {projectileX - cameraX - pradiusX, projectileY - cameraY - pradiusY}
    local bottomright = {projectileX - cameraX + pradiusX, projectileY - cameraY + pradiusY}
    gui.rectangle(topleft[1]*2, topleft[2]*2, (bottomright[1]-topleft[1])*2,(bottomright[2] - topleft[2])*2, 1, 0xFFFF80, -1)

    -- show projectile damage
    gui.text(tonumber(topleft[1])*2, tonumber(topleft[2]-8)*2, memory.readword('BUS', 0x7E0C2C + i*2), 0xFFFF80)
    
    -- show bomb timer
    if i >= 5 then
      gui.text(tonumber(topleft[1])*2, tonumber(topleft[2]-16)*2, memory.readbyte('BUS', 0x7E0C7C + i*2), 0xFFFF80)
    end;
  end;

  -- draw Samus' hitbox
  local radiusX, radiusY = memory.readsword('BUS', 0x7E0AFE), memory.readsword('BUS', 0x7E0B00);
  local topleft = {128 - radiusX, 112 - radiusY};
  local bottomright = {128 + radiusX, 112 + radiusY};
  gui.rectangle(topleft[1]*2,topleft[2]*2, (bottomright[1]-topleft[1])*2,(bottomright[2]-topleft[2])*2, 1, SAMUS_HITBOX_COLOR, -1);

  -- show current cooldown time
  local cooldown = memory.readword('BUS', 0x7E0CCC);
  if cooldown ~= 0 then
    gui.text(bottomright[1]*2, math.floor((topleft[2]+bottomright[2])/2-16)*2, cooldown, 0x00FF00);
  end;
  
  -- show current beam charge
  local charge = memory.readword('BUS', 0x7E0CD0);
  if charge ~= 0 then;
    gui.text(bottomright[1]*2, math.floor((topleft[2]+bottomright[2])/2-8)*2, charge, 0x00FF00);
  end;
  
  -- show recoil/invincibility
  local recoil, invincibility = memory.readword('BUS', 0x7E18AA), memory.readword('BUS', 0x7E18A8);
  if recoil ~= 0 then
    gui.text(bottomright[1]*2, math.floor((topleft[2]+bottomright[2])/2)*2, recoil, 0x00FFFF)
  elseif invincibility ~= 0 then
    gui.text(bottomright[1]*2, math.floor((topleft[2]+bottomright[2])/2)*2, invincibility, 0x00FFFF)
  end;
    
  local shine = memory.readbyte('BUS', 0x7E0A68);
  if shine ~= 0 then
    gui.text(bottomright[1]*2, math.floor((topleft[2]+bottomright[2])/2+8)*2, shine, 0x00FFFF);
  end;
      
  --if TASFlag ~= 0 then
    -- show horizontal speed
    --gui.text(topleft[1], topleft[2]-28, string.format("%X.%04X", memory.readword('BUS', 0x7E0B42), memory.readword('BUS', 0x7E0B44)), 0x00FFFF)
    
    -- show horizontal momentum
    --gui.text(topleft[1], topleft[2]-16, string.format("%X.%04X", memory.readword('BUS', 0x7E0B46), memory.readword('BUS', 0x7E0B48)), 0x00FFFF)
    
    -- show vertical speed
    --gui.text(topleft[1], bottomright[2]-4, string.format("%X.%04X", memory.readword('BUS', 0x7E0B2E), memory.readword('BUS', 0x7E0B2C)), 0x00FFFF)
    
    -- show speed booster level
    --gui.text(topleft[1], bottomright[2]+10, memory.readbyte('BUS', 0x7E0B3F), "#00FFFF")
    
    -- show in-game time
    -- TODO: the newly added enemy IDs from the enemy hitbox script will interfere with this text now
    --gui.text(216, 0, string.format("%d:%d:%d.%d", memory.readword('BUS', 0x7E09E0), memory.readword('BUS', 0x7E09DE), memory.readword('BUS', 0x7E09DC), memory.readword('BUS', 0x7E09DA)), 0xFFFFFF)
  --end
end;