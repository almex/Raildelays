package be.raildelays.logging;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.domain.xls.ExcelRow;

import java.util.List;

/**
 * Strongly typed logger interface.
 *
 * @author Almex
 */
public interface Logger extends org.slf4j.Logger {

    void info(String message, LineStop lineStop);

    void debug(String message, LineStop lineStop);

    void trace(String message, LineStop lineStop);

    void info(String message, List<LineStop> lineStops);

    void debug(String message, List<LineStop> lineStops);

    void trace(String message, List<LineStop> lineStops);

    void info(String message, ExcelRow excelRow);

    void debug(String message, ExcelRow excelRow);

    void trace(String message, ExcelRow excelRow);

    void info(String message, RouteLogDTO routeLog);

    void debug(String message, RouteLogDTO routeLog);

    void trace(String message, RouteLogDTO routeLog);

    void info(String message, TwoDirections twoDirections);

    void debug(String message, TwoDirections twoDirections);

    void trace(String message, TwoDirections twoDirections);

}
