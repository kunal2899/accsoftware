package com.accsoftware.rest.cash_book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.accsoftware.rest.expenses.Expenses;
import com.accsoftware.rest.expenses.ExpensesService;
import com.accsoftware.rest.incomes.Incomes;
import com.accsoftware.rest.incomes.IncomesService;

@CrossOrigin("http://localhost:4200")
@RestController
public class CashBookController {
	
	@Autowired
	ExpensesService es;
	
	@Autowired
	IncomesService is;
	
	@GetMapping("/{uid}/cash-book/{start}/{end}")
	public List<?> getAll(@PathVariable Long uid ,@PathVariable String start, @PathVariable String end){
		Locale locale = new Locale("EN","INDIA");
		DateFormatter df = new DateFormatter("yyyy-MM-dd");
		Date s = new Date();
		Date e = new Date();
		try {
			s = df.parse(start, locale);
			e =  df.parse(end, locale);
		} catch(Exception exp) {}
		
		List<Expenses> expList = es.getByRange(uid,"Cash", s, e);
		List<Incomes> incList = is.getByRange(uid,"Cash", s, e);
		
		List<CashBook>  cb = new ArrayList<CashBook>();
		
		
		for(Expenses x : expList) {
			CashBook c = new CashBook(x.getDate(),x.getAmount(),"Pay");
			cb.add(c);
		}
		
		for(Incomes x : incList) {
			CashBook c = new CashBook(x.getDate(),x.getAmount(),"Recieve");
			cb.add(c);
		}
		
		List <CashBook> sorted = cb.stream().sorted(Comparator.comparing(CashBook::getDate)).collect(Collectors.toList());
		
		return sorted;
	}
	
	@GetMapping("/{uid}/cash-book/cbal/{start}/{end}")
	public double getClosingBalance(@PathVariable Long uid,@PathVariable String start, @PathVariable String end) {
		Locale locale = new Locale("EN","INDIA");
		DateFormatter df = new DateFormatter("yyyy-MM-dd");
		Date s = new Date();
		Date e = new Date();
		try {
			s = df.parse(start, locale);
			e =  df.parse(end, locale);
		} catch(Exception exp) {}
		
		List<Expenses> expList = es.getByRange(uid,"Cash", s, e);
		List<Incomes> incList = is.getByRange(uid,"Cash", s, e);
		
		double cbal = 0;
		
		for(Expenses x : expList) {
			cbal -= x.getAmount();
		}
		
		for(Incomes x : incList) {
			cbal += x.getAmount();
		}
		
		return cbal;
	}
	
}
