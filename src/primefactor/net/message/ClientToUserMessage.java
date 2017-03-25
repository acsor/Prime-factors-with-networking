package primefactor.net.message;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by n0ne on 14/03/17.
 */
public class ClientToUserMessage extends Message {

	private static final String CONST_PROT_EQUALS = "=";
	private static final String CONST_PROT_MULT = "*";

	private BigInteger n;
	private List<BigInteger> factors;

	public ClientToUserMessage (BigInteger n, BigInteger... factors) {
		this.n = n;
		this.factors = new LinkedList<>(Arrays.asList(factors));
	}

	public BigInteger getN () {
		return n;
	}

	public List<BigInteger> getFactors () {
		return factors;
	}

	public boolean addFactor (BigInteger factor) {
		return factors.add(factor);
	}

	public boolean addFactors (Collection<BigInteger> factors) {
		return this.factors.addAll(factors);
	}

	@Override
	public String toString () {
		final StringBuilder b = new StringBuilder();

		b.append(n)
				.append(CONST_PROT_SPACE + CONST_PROT_EQUALS + CONST_PROT_SPACE);

		if (factors.size() >= 1) {
			b.append(factors.get(0));

			for (int i = 1; i < factors.size(); i++) {
				b.append(CONST_PROT_SPACE)
				.append(CONST_PROT_MULT)
				.append(CONST_PROT_SPACE)
				.append(factors.get(i));
			}
		}

		b.append(CONST_PROT_NEWLINE);

		return b.toString();
	}

}
