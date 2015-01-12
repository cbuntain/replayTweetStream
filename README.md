# replayTweetStream
Given a gzipped file of tweets, replay them as though you were streaming them from Twitter's data stream

Currently, only supports a single GZipped file and replays at the per-second level with gaps between messages as determined by their created_at timestamps.