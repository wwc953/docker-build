local key = ARGV[1]
local qName = ARGV[2]
redis.call('ZREM', qName .. 'BACKUP', key)
redis.call('HDEL', qName .. 'CONTEXT', key)