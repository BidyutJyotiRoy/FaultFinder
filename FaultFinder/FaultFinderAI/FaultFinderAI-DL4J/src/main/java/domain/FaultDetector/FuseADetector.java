/**
 * 
 */
package domain.FaultDetector;

import java.io.IOException;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import clasDC.faults.FaultNames;

/**
 * @author m.c.kunkel
 *
 */
public class FuseADetector extends FaultDetector {

	public FuseADetector() {
		this(0);

	}

	public FuseADetector(int superlayer) {
		this.desiredFault = FaultNames.FUSE_A;
		this.superlayer = superlayer;
		try {
			init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getClassifierPredictions()
	 */
	@Override
	public INDArray getClassifierPredictions(INDArray data) {
		return classClassifier.output(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getObjectPredictions()
	 */
	@Override
	public INDArray getObjectPredictions(INDArray data) {

		INDArrayIndex[] indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all(),
				NDArrayIndex.interval(faultSlidingInformation.getXStart()[0],
						faultSlidingInformation.getXEnd()[faultSlidingInformation.getXLength() - 1]) };
		INDArray slice = data.get(indexs).dup();
		return objectClassifier.output(slice);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see domain.FaultDetector.FaultDetector#getSliderPredictions()
	 */
	@Override
	public INDArray getSliderPredictions(INDArray data) {
		INDArray ret = this.desiredFault.getPossiblePositions().dup();
		for (int i = 0; i < faultSlidingInformation.getXLength(); i++) {
			INDArrayIndex[] indexs = new INDArrayIndex[] { NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all(),
					NDArrayIndex.interval(faultSlidingInformation.getXStart()[i],
							faultSlidingInformation.getXEnd()[i]) };
			INDArray slice = data.get(indexs).dup();
			INDArray sliderPredictions = slidingClassifier.output(slice);
			ret.putScalar(0, i, sliderPredictions.getDouble(1));
		}

		return ret;
	}

}
