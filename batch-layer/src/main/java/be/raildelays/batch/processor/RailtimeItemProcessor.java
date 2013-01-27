package be.raildelays.batch.processor;

import java.util.Date;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.dozer.Mapper;
import org.springframework.batch.item.ItemProcessor;

import be.raildelays.domain.dto.RouteLogDTO;

public class RailtimeItemProcessor implements ItemProcessor<String, RouteLogDTO> {

	@Resource
	Mapper mapper;
	
	@Resource
	Validator validator;

	@Override
	public RouteLogDTO process(final String item) throws Exception {
		
		return new RouteLogDTO("", new Date());
	}
	
	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
