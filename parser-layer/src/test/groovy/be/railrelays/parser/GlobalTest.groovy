package be.railrelays.parser

import org.apache.log4j.Logger

import be.raildelays.domain.railtime.Direction
import be.raildelays.httpclient.RequestStreamer
import be.raildelays.httpclient.impl.RailtimeRequestStreamer
import be.raildelays.parser.StreamParser;
import be.raildelays.parser.impl.RailtimeStreamParser
import be.raildelays.util.ParsingUtil


Logger log = Logger.getLogger(GlobalTest.class)

StreamParser parser;
RequestStreamer streamer = new RailtimeRequestStreamer();
Date date = ParsingUtil.parseDate('17/01/2012');
Direction direction;
parser = new RailtimeStreamParser(streamer.getDelays("466", date));
direction = parser.parseDelay("466", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("467", date));
direction = parser.parseDelay("467", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("468", date));
direction = parser.parseDelay("468", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("514", date));
direction = parser.parseDelay("514", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("515", date));
direction = parser.parseDelay("515", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("477", date));
direction = parser.parseDelay("477", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("478", date));
direction = parser.parseDelay("478", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("529", date));
direction = parser.parseDelay("529", date);
log.info(direction);
parser = new RailtimeStreamParser(streamer.getDelays("530", date));
direction = parser.parseDelay("530", date);
log.info(direction);