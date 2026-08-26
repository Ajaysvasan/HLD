package project.two.wallet.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.two.wallet.DTO.Wallets.WalletBalance;
import project.two.wallet.DTO.Wallets.WalletBalanceRequest;
import project.two.wallet.Security.UserPrincipal;
import project.two.wallet.Service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
	private final WalletService walletService;

	public WalletController(WalletService walletService) {
		this.walletService = walletService;
	}

	@PostMapping("/add")
	public ResponseEntity<WalletBalance> createWallet(Authentication authentication) {
		Long userId = userId(authentication);
		return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(userId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<WalletBalance>> getAllWallets(Authentication authentication) {
		Long userId = userId(authentication);
		return ResponseEntity.ok(walletService.getAllWallets(userId));
	}

	@PostMapping("/balance")
	public ResponseEntity<WalletBalance> getWalletBalance(@RequestBody WalletBalanceRequest walletBalanceRequest,
			Authentication authentication) {
		Long userId = userId(authentication);
		WalletBalance balance = walletService.getWalletBalance(userId, walletBalanceRequest.getWalletId());
		return ResponseEntity.ok(balance);
	}

	private Long userId(Authentication authentication) {
		return ((UserPrincipal) authentication.getPrincipal()).getUserId();
	}
}
