package primefactor.net.message;

import java.math.BigInteger;

/**
 * Created by n0ne on 14/03/17.
 */
public class ClientToUserMessage extends Message {

	private BigInteger product;
	private BigInteger[] factors;

	public ClientToUserMessage (BigInteger product, BigInteger... factors) {
		this.product = product;
		this.factors = factors;
	}

	public BigInteger getProduct () {
		return product;
	}

	public BigInteger[] getFactors () {
		return factors;
	}

}
