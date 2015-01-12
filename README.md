# replayTweetStream
Given a gzipped file of tweets, replay them as though you were streaming them from Twitter's data stream

Currently, only supports a single GZipped file and replays at the per-second level with gaps between messages as determined by their created_at timestamps. Also, for emission, it just prints out to stdout. I will extend this to Kafka or an interface that makes the sink configurable.