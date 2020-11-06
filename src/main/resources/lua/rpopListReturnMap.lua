-- 未成功！！！
local map = {}
local this = map
function this:new()
    o = {}
    setmetatable(o, self)
    self.__index = self
    self.count = 0
    return o
end

function this:insert(k, v)
    if nil == self[k] then
        --table.insert(self,{a = b})
        self[k] = v
        self.count = self.count + 1
        print("insert")
    end
    print("insert end")
end


local array = {}
local arrayLength = redis.call("llen", KEYS[1])
if (arrayLength > tonumber(ARGV[1])) then
    arrayLength = tonumber(ARGV[1])
end
for i = 1, arrayLength do
    array[i] = redis.call("rpop", KEYS[1])
end

local resultMap = map:new()
resultMap:insert("size", redis.call("llen", KEYS[1]))
resultMap:insert("list", array)

return resultMap