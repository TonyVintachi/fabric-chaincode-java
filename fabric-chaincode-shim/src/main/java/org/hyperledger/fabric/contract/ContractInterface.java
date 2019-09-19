/*
Copyright IBM Corp., DTCC All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
*/

package org.hyperledger.fabric.contract;

import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ChaincodeException;

/**
 * All Contracts should implement this interface, in addition to the
 * {@link Contract} annotation.
 * <p>
 * All the of the methods on this is inteface have default implementations; for
 * many contracts it may not be needed to sub-class these.
 * <p>
 * Each method on the Contract that is marked with the {@link Transaction}
 * annotation is considered a Transaction Function. This is eligible for
 * calling. Each transaction function is supplied with it's first parameter
 * being a {@link org.hyperledger.fabric.contract.Context} the other parameters
 * are at the developer's discretion.
 * <p>
 * The sequence of calls is
 *
 * <pre>
 * createContext()  -&gt; beforeTransaction() -&gt; the transaction function -&gt; afterTransaction()
 * </pre>
 * <p>
 * If any of these functions throws an exception it is considered an error case,
 * and the whole transaction is failed. The {@link org.hyperledger.fabric.contract.Context} is
 * a very important object as it provides transactional context for access to current transaction id,
 * ledger state, etc.
 * <p>
 * <b>Note on Threading</b>
 * <p>
 * All code should be 'Thread Friendly'. Each method must not rely on instance
 * fields or class side variables for storage. Nor should they use any
 * ThreadLocal Storage. Ledger data is stored via the ledger api available via
 * the {@link Context}.
 * <p>
 * If information needs to be passed from the {@link #beforeTransaction(Context)}
 * {@link #afterTransaction(Context, Object)} or between separate transaction functions when
 * called directory, then a subclass of the
 * {@link Context} should be provided.
 */
public interface ContractInterface {

    /**
     * Create context from {@link ChaincodeStub}, default impl provided, but can be
     * overwritten by contract
     *
     * @param stub Instance of the ChaincodeStub to use for this transaction
     * @return instance of the context to use for the current transaciton being
     *         executed
     */
    default Context createContext(ChaincodeStub stub) {
        return ContextFactory.getInstance().createContext(stub);
    }

    /**
     * Invoked for any transaction that does not exist.
     *
     * This will throw an exception. If you wish to alter the exception thrown or if
     * you wish to consider requests for transactions that don't exist as not an
     * error, subclass this method.
     *
     * @param ctx the context as created by {@link #createContext(ChaincodeStub)}.
     */
    default void unknownTransaction(Context ctx) {
        throw new ChaincodeException("Undefined contract method called");
    }

    /**
     * Invoked once before each transaction.
     *
     * Any exceptions thrown will fail the transaction, and neither the required
     * transaction or the {@link #afterTransaction(Context, Object)} will be called
     *
     * @param ctx the context as created by {@link #createContext(ChaincodeStub)}.
     */
    default void beforeTransaction(Context ctx) {
    }

    /**
     * Invoked once after each transaction.
     *
     * Any exceptions thrown will fail the transaction.
     *
     * @param ctx    the context as created by {@link #createContext(ChaincodeStub)}.
     * @param result The object returned from the transaction function if any. As
     *               this is a Java object and therefore pass-by-reference it is
     *               possible to modify this object.
     */
    default void afterTransaction(Context ctx, Object result) {
    }
}
