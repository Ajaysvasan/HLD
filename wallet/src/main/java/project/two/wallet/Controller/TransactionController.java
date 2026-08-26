package project.two.wallet.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.two.wallet.DTO.Transactions.DepositRequest;
import project.two.wallet.DTO.Transactions.SendMoneyRequest;
import project.two.wallet.DTO.Transactions.TransactionResponse;
import project.two.wallet.DTO.Transactions.WithdrawRequest;
import project.two.wallet.Security.UserPrincipal;
import project.two.wallet.Service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping("/send")
	public ResponseEntity<TransactionResponse> send(@RequestBody SendMoneyRequest request,
			Authentication authentication) {
		return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.send(userId(authentication), request));
	}

	@PostMapping("/deposit")
	public ResponseEntity<TransactionResponse> deposit(@RequestBody DepositRequest request,
			Authentication authentication) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(transactionService.deposit(userId(authentication), request));
	}

	@PostMapping("/withdraw")
	public ResponseEntity<TransactionResponse> withdraw(@RequestBody WithdrawRequest request,
			Authentication authentication) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(transactionService.withdraw(userId(authentication), request));
	}

	@GetMapping("/history")
	public ResponseEntity<List<TransactionResponse>> history(@RequestParam(required = false) Long otherUserId,
			@RequestParam(required = false) Long otherWalletId, @RequestParam(required = false) Long walletId,
			@RequestParam(defaultValue = "false") boolean self, @RequestParam(required = false) String sort,
			Authentication authentication) {
		return ResponseEntity.ok(transactionService.getHistory(userId(authentication), otherUserId, otherWalletId,
				walletId, self, sort));
	}

	private Long userId(Authentication authentication) {
		return ((UserPrincipal) authentication.getPrincipal()).getUserId();
	}
}
