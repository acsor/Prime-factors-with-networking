package primefactor.net.message;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by n0ne on 14/03/17.
 */
public class ClientToUserMessage extends Message {

	private BigInteger product;
	private List<BigInteger> factors;

	public ClientToUserMessage (BigInteger product, BigInteger... factors) {
		this.product = product;
		this.factors = new LinkedList<>(Arrays.asList(factors));
	}

	public BigInteger getProduct () {
		return product;
	}

	public List<BigInteger> getFactors () {
		return factors;
	}

	public boolean addFactor (BigInteger factor) {
		return factors.add(factor);
	}

}
