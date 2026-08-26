package project.two.wallet.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import project.two.wallet.DTO.Wallets.WalletBalance;
import project.two.wallet.Entity.Wallet;
import project.two.wallet.Exception.WalletNotFoundException;
import project.two.wallet.Repository.WalletRepository;

@Service
public class WalletService {
	private final WalletRepository walletRepository;

	public WalletService(WalletRepository walletRepository) {
		this.walletRepository = walletRepository;
	}

	public WalletBalance createWallet(Long userId) {
		Wallet wallet = new Wallet();
		wallet.setUserId(userId);
		wallet.setBalance(0L);
		Wallet saved = walletRepository.save(wallet);
		return toWalletBalance(saved);
	}

	public List<WalletBalance> getAllWallets(Long userId) {
		return walletRepository.findAllByUserId(userId).stream().map(this::toWalletBalance)
				.collect(Collectors.toList());
	}

	public WalletBalance getWalletBalance(Long userId, Long walletId) {
		if (!walletRepository.existsByUserIdAndWalletId(userId, walletId)) {
			throw new WalletNotFoundException("Wallet not found: " + walletId);
		}
		return walletRepository.getWalletBalance(walletId)
				.orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
	}

	private WalletBalance toWalletBalance(Wallet wallet) {
		WalletBalance walletBalance = new WalletBalance();
		walletBalance.setWalletId(wallet.getWalletId());
		walletBalance.setBalance(wallet.getBalance());
		return walletBalance;
	}
}
