package com.example.demo.controller;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entities.Users;
import com.example.demo.services.UsersService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {
	@Autowired
	UsersService service;
	
	@GetMapping("/pay")
	public String pay() {
		
		return "pay";
	}
	
	@GetMapping("/payment-success")
	public String paymentSuccess(HttpSession session) {
		String mail = (String) session.getAttribute("email");
		Users u = service.getUser(mail);
			u.setPremium(true);
			service.updateUser(u);
		return "customerHome";
	}

	@GetMapping("/payment-failure")
	public String paymentFailure(){
		return "customerHome";
	}
	
	@SuppressWarnings("finally")
	@PostMapping("/createOrder")
	@ResponseBody
	public String createOrder() {
		
		int amount = 5000;
		Order order=null;
		try {
			RazorpayClient razorpay=new RazorpayClient("rzp_test_IQvo4kDJhQfYEI","C24TnnenrPF5MzVKrJwYNiwQ");
			
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amount*100); 
			orderRequest.put("currency","INR");
			orderRequest.put("receipt","order_rcptid_11");	
			
			order = razorpay.orders.create(orderRequest);
			
			

		} catch (RazorpayException e) {
			e.printStackTrace();

		}
		finally {
			return order.toString();
		}
	}

	@PostMapping("/verify")
		@ResponseBody
		public boolean verifyPayment(@RequestParam String orderId, @RequestParam String paymentId, @RequestParam String signature){
		     try {
			     //Initialize Razorpay client with your API key and secret
			     RazorpayClient razorpayClient = new  RazorpayClient("rzp_test_IQvo4kDJhQfYEI","C24TnnenrPF5MzVKrJwYNiwQ");
			     //Create Signature verification  data String
			     String verificationDate = orderId + "|" + paymentId;

			//use Razorpay's utility function to verify the signature 	 
			boolean isvalidSignature = Utils.verifySignature(verificationDate, signature,"C24TnnenrPF5MzVKrJwYNiwQ");

			return isvalidSignature;
		} catch (RazorpayException e) {
			e.printStackTrace();
			return false;
		}
    }
}
