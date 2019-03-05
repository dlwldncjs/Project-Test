package AC_DicomData.DicomIO;

import java.io.BufferedInputStream;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class AC_DicomReader {

	static Logger logger = Logger.getLogger(AC_DicomReader.class);

	private static final int ID_OFFSET = 128; // location of "DICM"
	private static final String DICM = "DICM";
	// private static final int IMPLICIT_VR = 0x2D2D; // '--'
	// private AC_DicomDictionary..// m_DicomDic = new AC_DicomDictionary();;

	private String m_sFilePath = null;
	private File m_sFile = null;
	BufferedInputStream m_bisInputStream;
	private boolean m_flagFileEnd = false;
//	private int ifReadLoactaion =0;
	private boolean m_bLittleEndian = true;
	/// TransferSyntaxUID
	private String m_sTransferSyntaxUID = null;
	private boolean m_bigEndianTransferSyntax = false;
	private boolean m_Compressed = false;

	private static String m_byteSplit = "\\\\";

	private int m_VR;
	private int m_nElementLength = 0;

	private int m_TageID;
	private int m_nLocation = 0;

	public AC_DicomReader() {
	}

	public AC_DicomReader(String sFilePath) {
		readDCMFile(sFilePath);
	}

	public AC_DicomReader(File sFilePath) {
		readDCMFile(sFilePath);
	}

	public boolean readDCMFile(File input) {
		if (m_sFilePath == null)
			m_sFilePath = input.getAbsolutePath();
		return init(input);
	}

	public boolean readDCMFile(String input) {
		m_sFilePath = input;
		m_sFile = new File(m_sFilePath);
		return readDCMFile(m_sFile);
	}

	public void close() {
		try {
			m_bisInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean init(File sFilePath) {
		FileInputStream fis = null;

		if (!AC_DicomDictionary.isSetup())
			AC_DicomDictionary.setupList();

		try {
			fis = new FileInputStream(sFilePath);
			if (fis.getChannel().size() == 0 || fis.getChannel().size() < AC_DCMStandard.DCMI_LEN) {
				logger.error(m_sFilePath + " - This File Too Small");
				return false;
			}

			m_bisInputStream = new BufferedInputStream(fis);
			m_bisInputStream.mark(400000);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// fis.close();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void moveHeaderStart() throws IOException {
		skip(ID_OFFSET);

		if (!getString(4).equals(DICM)) {
			if (m_bisInputStream == null)
				m_bisInputStream.close();
			if (m_bisInputStream != null) {
				m_bisInputStream.reset();
				m_flagFileEnd = false;
			}
		}

	}

	private boolean checkHeaderStart() throws IOException {
		skip(ID_OFFSET);
		if (!getString(4).equals(DICM)) {
			if (m_bisInputStream == null) {
				m_bisInputStream.close();
				return false;
			}

			if (m_bisInputStream != null) {
				m_bisInputStream.reset();
				// m_flagFileEnd = false;
				return false;
			}
		}
		return true;
	}

	private void checkTSUID() throws IOException {

		m_sTransferSyntaxUID = getString(m_nElementLength);

		if (m_sTransferSyntaxUID.indexOf("1.2.840.10008.1.2.2") >= 0)
			m_bigEndianTransferSyntax = true;

		if (m_sTransferSyntaxUID.indexOf("1.2.840.10008.1.2.4") >= 0) {
			m_Compressed = true;
			logger.error("Compressed Dicom");
			JOptionPane.showMessageDialog(null, "Compressed DCM은 아직 지원하지 않습니다.", "File Reader Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public AC_DcmStructure getAttirbutes2() throws IOException {
		m_bisInputStream.reset();
		m_flagFileEnd = false;

		List<String[]> tmp = new ArrayList<>();

		while (!m_flagFileEnd) {
			int tag = getNextTag();
			logger.info(String.format("TAG : %08x , Lengt : %d", tag, m_nElementLength));
			// System.out.println(String.format("TAG : %08x , Lengt : %d",
			// tag,m_nElementLength));
			if (tag == AC_Tag.TransferSyntaxUID) {
				checkTSUID();
				String[] value = { Integer.toString(AC_VR.UI), Integer.toString(m_nElementLength),
						m_sTransferSyntaxUID };
				tmp.add(value);
			}

			else if (AC_Tag.PixelData == tag)
				break;
			else {
				String[] value = { Integer.toString(m_VR), Integer.toString(m_nElementLength), getValue(m_VR) };
				for (String tmp3 : value) {
					System.out.print(String.format("TAG : %08x ", tag) + tmp3 + " ");
				}
				System.out.println(" ");
				tmp.add(value);
			}
		}
		return null;
	}

	public AC_DcmStructure getAttirbutes() throws IOException {
		AC_DcmStructure ouptutAttributes = new AC_DcmStructure();
		ouptutAttributes.setFilePath(m_sFilePath);
		m_bisInputStream.reset();
		m_flagFileEnd = false;

		checkHeaderStart();

		while (!m_flagFileEnd) {
			int tag = getNextTag();
			logger.info(String.format("TAG : %08x , Lengt : %d", tag, m_nElementLength));
			// System.out.println(String.format("TAG : %08x , Lengt : %d",
			// tag,m_nElementLength));

			if (tag == AC_Tag.TransferSyntaxUID) {
				checkTSUID();
				String[] value = {Integer.toString(AC_VR.UI), m_sTransferSyntaxUID};
				ouptutAttributes.setAttribute(tag, value);
			} else if (m_VR == AC_VR.SQ || AC_DicomDictionary.getTagVR(tag) == AC_VR.SQ) {

				String[] seqncvalue = {Integer.toString(m_VR), Integer.toString(m_nElementLength)};
				ouptutAttributes.setAttribute(tag, seqncvalue);
				ouptutAttributes.setSequenceValue(tag, readSequnce(m_nElementLength));

			} else if (tag == AC_Tag.PixelData) {
				m_flagFileEnd = true;
				// ouptutAttributes.setPixelData(getPixelData(m_nElementLength),m_VR);
				// break;
			} else if (tag == AC_Tag.Item) {

			} else {
				String[] value = {Integer.toString(m_VR), getValue(m_VR)};
				if (tag == AC_Tag.SOPInstanceUID) {
					System.out.println("SOP Instace UID" + value[1]);
				}
				ouptutAttributes.setAttribute(tag, value);
				// System.out.println(String.format("TAG : %08x , Lengt : %d, valuse : %s",
				// tag,m_nElementLength, value[1]));
			}
		}

		System.out.println("done..read");
		// ouptutAttributes.printInfo("in");
		return ouptutAttributes;
	}

	public byte[] getDicomInfo(AC_DcmStructure info) throws IOException {
		m_bisInputStream.reset();
		m_flagFileEnd = false;

		byte[] bPixelData = null;

		boolean decodingTags = true;

		moveHeaderStart();

		while (decodingTags) {
			int iTmpDicom = getNextTag();
			if (iTmpDicom == AC_Tag.TransferSyntaxUID) {
				checkTSUID();
			}

			else if (m_VR == AC_VR.SQ) {
				readSequnce(iTmpDicom);
			}

			else {
				switch (iTmpDicom) {
				case AC_Tag.InstanceNumber:
				case AC_Tag.SeriesInstanceUID:
				case AC_Tag.WindowCenter:
				case AC_Tag.WindowWidth:
				case AC_Tag.Rows:
				case AC_Tag.Columns:
				case AC_Tag.BitsAllocated:
				case AC_Tag.BitsStored:
				case AC_Tag.RescaleIntercept:
				case AC_Tag.RescaleSlope:
				case AC_Tag.StudyInstanceUID:
				case AC_Tag.StudyTime:
				case AC_Tag.StudyDate:
				case AC_Tag.PatientID:
				case AC_Tag.StudyDescription:
				case AC_Tag.SeriesDescription:
				case AC_Tag.Modality:
				case AC_Tag.SeriesNumber:
				case AC_Tag.PatientsName:
				case AC_Tag.PatientsBirthDate:
				case AC_Tag.PatientsSex:
				case AC_Tag.InstitutionName:
				case AC_Tag.StudyID:
				case AC_Tag.SliceThickness:
				case AC_Tag.SliceLocation:
				case AC_Tag.SeriesDate:
				case AC_Tag.SeriesTime:
				case AC_Tag.XRayTubeCurrent:
				case AC_Tag.KVP:
				case AC_Tag.MagneticFieldStrength:
				case AC_Tag.RepetitionTime:
				case AC_Tag.EchoTime:
				case AC_Tag.PixelRepresentation:
				case AC_Tag.SamplesperPixel:
				case AC_Tag.ImageOrientationPatient:
				case AC_Tag.ReconstructionDiameter:
					info.changeAttribute(iTmpDicom, getValue(m_VR));
					break;
				case AC_Tag.PixelSpacing:
					info.changeAttribute(iTmpDicom, getValue(m_VR));
					break;
				case AC_Tag.PixelData:

					bPixelData = new byte[m_nElementLength];
					for (int i = 0; i < m_nElementLength; i++) {
						bPixelData[i] = (byte) m_bisInputStream.read();
					}
					decodingTags = false;
					break;

				default:
					skip(m_nElementLength);
					break;
				}
			}
		}
		m_bisInputStream.close();
		return bPixelData;
	}

	public void getTableDicomInfo(AC_DcmStructure info) throws IOException {
		m_bisInputStream.reset();
		m_flagFileEnd = false;

		byte[] bPixelData = null;
		boolean decodingTags = true;

		moveHeaderStart();

		while (decodingTags) {
			int iTmpDicom = getNextTag();
			if (m_VR == AC_VR.SQ) {
				info.changeAttribute(iTmpDicom, getValue(m_VR));
				int iTmpTag = iTmpDicom;
				AC_DcmStructure tmp = readSequnce(iTmpDicom);
				info.setSequenceValue(iTmpTag, tmp);

			} else {
				String value = getValue(m_VR);
				info.changeAttribute(iTmpDicom, value);
				System.out.println(
						String.format("TAG : %08x , Lengt : %d, value : %s ", iTmpDicom, m_nElementLength, value));
			}

			if (iTmpDicom == AC_Tag.PixelData) {
				info.changeAttribute(iTmpDicom, getValue(m_VR));
				decodingTags = false;

			}
		}
		m_bisInputStream.close();
	}

	public byte[] getPixelData() throws IOException {
		AC_DcmStructure ouptutAttributes = new AC_DcmStructure();
		ouptutAttributes.setFilePath(m_sFilePath);
		m_bisInputStream.reset();
		m_flagFileEnd = false;

		checkHeaderStart();

		while (!m_flagFileEnd) {
			int tag = getNextTag();

			if (tag == AC_Tag.PixelData) {
				return getPixelData(m_nElementLength);
			} else {
				skip(m_nElementLength);
			}
		}
		return null;
	}

	private AC_DcmStructure readSequnce(int sqenceLenght) throws IOException {
		AC_DcmStructure outputSequnce = new AC_DcmStructure();

		if (sqenceLenght == -1) {

			while (true) {
				int tag = getNextTag();

				if (tag == AC_Tag.SequenceDelimitationItem) {
					break;
				} else if (m_VR == AC_VR.SQ || AC_DicomDictionary.getTagVR(tag) == AC_VR.SQ) {
					String[] seqncvalue = { Integer.toString(m_VR), Integer.toString(m_nElementLength) };
					outputSequnce.setAttribute(tag, seqncvalue);
					outputSequnce.setSequenceValue(tag, readSequnce(m_nElementLength));
				} else {
					String[] value = { Integer.toString(m_VR), getValue(m_VR) };
					outputSequnce.setAttribute(tag, value);
				}

				logger.debug(String.format("In SQ TYPE : A  TAG : %08x , Lengt : %d", tag, m_nElementLength));

			}
		} else {
			int startSQLocatoin = m_nLocation;
			int SQLenght = m_nElementLength;

			while (m_nLocation - startSQLocatoin < SQLenght) {
				int tag = getNextTag();
				int tmpVR = 0;
				if (m_VR == AC_VR.Undefined)
					tmpVR = AC_DicomDictionary.getTagVR(tag);

				if (tag == AC_Tag.Item) {
					String[] itmeValue = { Integer.toString(AC_VR.Undefined), Integer.toString(m_nElementLength) };
					outputSequnce.setAttribute(tag, itmeValue);
				} else if (m_VR == AC_VR.SQ || AC_DicomDictionary.getTagVR(tag) == AC_VR.SQ) {
					String[] seqncvalue = { Integer.toString(m_VR), Integer.toString(m_nElementLength) };
					outputSequnce.setAttribute(tag, seqncvalue);
					outputSequnce.setSequenceValue(tag, readSequnce(m_nElementLength));
				} else {
					String[] value = { Integer.toString(m_VR), getValue(m_VR) };
					outputSequnce.setAttribute(tag, value);
				}

				logger.debug(String.format("In SQ TYPE : B  TAG : %08x , Lengt : %d", tag, m_nElementLength));

			}

		}

		return outputSequnce;
	}

	private int getNextTag() throws IOException {
		int igroupWord = getShort();
		if (igroupWord == 0x0800 && m_bigEndianTransferSyntax) {
			m_bLittleEndian = false;
			igroupWord = 0x0008;
		}
		int ielementWord = getShort();

		int tag = igroupWord << 16 | ielementWord;

		m_nElementLength = getLength();

		m_TageID = tag;

		return tag;

	}

	int getLength() throws IOException {
		int b0 = getByte();
		int b1 = getByte();

		int b2 = getByte();
		int b3 = getByte();

		// We cannot know whether the VR is implicit or explicit
		// without the full DICOM Data Dictionary for public and
		// private groups.

		// We will assume the VR is explicit if the two bytes
		// match the known codes. It is possible that these two
		// bytes are part of a 32-bit length for an implicit VR.
		m_VR = (b0 << 8) + b1;

		switch (m_VR) {

		case AC_VR.SQ:

			return getInt();

		case AC_VR.OB:
		case AC_VR.OW:
		case AC_VR.OF:
		case AC_VR.UN:
		case AC_VR.UT:

			// Explicit VR with 32-bit length if other two bytes are zero
			if ((b2 == 0) || (b3 == 0)) {
				return getInt();
			}

		case AC_VR.AE:
		case AC_VR.AS:
		case AC_VR.AT:
		case AC_VR.CS:
		case AC_VR.DA:
		case AC_VR.DS:
		case AC_VR.DT:
		case AC_VR.FD:
		case AC_VR.FL:
		case AC_VR.IS:
		case AC_VR.LO:
		case AC_VR.LT:
		case AC_VR.PN:
		case AC_VR.SH:
		case AC_VR.SL:
		case AC_VR.SS:
		case AC_VR.ST:
		case AC_VR.TM:
		case AC_VR.UI:
		case AC_VR.UL:
		case AC_VR.US:
		case AC_VR.QQ:
			// Explicit vr with 16-bit length
			if (m_bLittleEndian)
				return ((b3 << 8) + b2);
			else
				return ((b2 << 8) + b3);

		default:
			// Implicit VR with 32-bit length...
			m_VR = AC_VR.Undefined;
			if (m_bLittleEndian)
				return ((b3 << 24) + (b2 << 16) + (b1 << 8) + b0);
			else
				return ((b0 << 24) + (b1 << 16) + (b2 << 8) + b3);
		}
	}

	private String getValue(int iVR) throws IOException {
		if (m_nElementLength == -1 || m_nElementLength == 0)
			return "";

		if (AC_VR.Undefined == iVR) {
			// m_DicomDic = new AC_DicomDictionary();

			m_VR = iVR = AC_DicomDictionary.getTagVR(m_TageID);
		}

		String sValue = "";
		int ivm = 0;
		String fullS = "";

		byte b0 = 0;
		byte b1 = 0;

		switch (iVR) {
		case AC_VR.OB:
		case AC_VR.UN:
			ivm = m_nElementLength / 2;
			fullS = "";

			b0 = (byte) getByte();
			b1 = (byte) getByte();
			fullS = Byte.toString(b0) + m_byteSplit + Byte.toString(b1);
			//
			for (int i = 1; i < ivm; i++) {
				b0 = (byte) getByte();
				b1 = (byte) getByte();
				fullS += m_byteSplit + Byte.toString(b0) + m_byteSplit + Byte.toString(b1);
			}
			sValue = fullS;
			// alue = getString(m_nElementLength);
			break;
		case AC_VR.UL:
			sValue = (Integer.toString(getInt()));
			break;
		case AC_VR.FD:
			ivm = m_nElementLength / 8;
			fullS = "";

			fullS += Double.toString(getDouble());
			//
			for (int i = 1; i < ivm; i++) {
				fullS += m_byteSplit + Double.toString(getDouble());
			}
			sValue = fullS;
			break;
		case AC_VR.FL:

			if (m_nElementLength == 4)
				sValue = Float.toString(getFloat());
			else {
				sValue = "";
				int n = m_nElementLength / 4;
				for (int i = 0; i < n; i++)
					sValue += Float.toString(getFloat()) + m_byteSplit;
			}
			break;

		case AC_VR.AE:
		case AC_VR.AS:
		case AC_VR.AT:
		case AC_VR.CS:
		case AC_VR.DA:
		case AC_VR.DS:
		case AC_VR.DT:
		case AC_VR.IS:
		case AC_VR.LO:
		case AC_VR.LT:
		case AC_VR.PN:
		case AC_VR.SH:
		case AC_VR.ST:
		case AC_VR.TM:
		case AC_VR.UI:
			sValue = getString(m_nElementLength);
			break;
		case AC_VR.US:
			if (m_nElementLength == 2)
				sValue = Integer.toString(getShort());
			else {
				sValue = "";
				int n = m_nElementLength / 2;
				for (int i = 0; i < n; i++)
					sValue += Integer.toString(getShort()) + m_byteSplit;
			}
			break;
		case AC_VR.Undefined:
			ivm = m_nElementLength / 2;
			fullS = "";

			b0 = (byte) getByte();
			b1 = (byte) getByte();
			fullS = Byte.toString(b0) + m_byteSplit + Byte.toString(b1);
			//
			for (int i = 1; i < ivm; i++) {
				b0 = (byte) getByte();
				b1 = (byte) getByte();
				fullS += m_byteSplit + Byte.toString(b0) + m_byteSplit + Byte.toString(b1);
			}
			sValue = fullS;
			break;
		case AC_VR.SQ:
			sValue = "sqens";
			break;
		/*
		 * boolean privateTag = ((tag>>16)&1)!=0; if (tag!=ICON_IMAGE_SEQUENCE &&
		 * !privateTag) break;
		 */
		// else fall through and skip icon image sequence or private sequence
		default:
			skip((long) m_nElementLength);
			sValue = "defult";
		}

		sValue = sValue.trim();

		if (sValue.equals(""))
			sValue = "";

		return sValue;
	}

	private void skip(long lskipCount) {
		m_nLocation += lskipCount;

		while (lskipCount > 0)
			try {
				lskipCount -= m_bisInputStream.skip(lskipCount);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private byte[] getPixelData(int length) throws IOException {

		byte[] arrPixelData = new byte[m_nElementLength];

		for (int i = 0; i < m_nElementLength; i++) {
			arrPixelData[i] = (byte) m_bisInputStream.read();
		}

		return arrPixelData;
	}

	String getString(int length) throws IOException {
		byte[] buf = new byte[length];

		for (int i = 0; i < length; i++) {
			buf[i] = (byte) getByte();
		}

		m_nLocation += length;

		String tmp = new String(buf);
		String newTmp = tmp.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
		return newTmp;
	}

	int getByte() throws IOException {
		int b = m_bisInputStream.read();
		if (b == -1) {
			m_flagFileEnd = true;
			// throw new IOException("unexpected EOF");

		}
		++m_nLocation;
		return b;
	}

	int getShort() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		if (m_bLittleEndian)
			return ((b1 << 8) + b0);
		else
			return ((b0 << 8) + b1);
	}

	final int getInt() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		if (m_bLittleEndian)
			return ((b3 << 24) + (b2 << 16) + (b1 << 8) + b0);
		else
			return ((b0 << 24) + (b1 << 16) + (b2 << 8) + b3);
	}

	double getDouble() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		int b4 = getByte();
		int b5 = getByte();
		int b6 = getByte();
		int b7 = getByte();
		long res = 0;
		if (m_bLittleEndian) {
			res += b0;
			res += (((long) b1) << 8);
			res += (((long) b2) << 16);
			res += (((long) b3) << 24);
			res += (((long) b4) << 32);
			res += (((long) b5) << 40);
			res += (((long) b6) << 48);
			res += (((long) b7) << 56);
		} else {
			res += b7;
			res += (((long) b6) << 8);
			res += (((long) b5) << 16);
			res += (((long) b4) << 24);
			res += (((long) b3) << 32);
			res += (((long) b2) << 40);
			res += (((long) b1) << 48);
			res += (((long) b0) << 56);
		}
		return Double.longBitsToDouble(res);
	}

	float getFloat() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		int res = 0;
		if (m_bLittleEndian) {
			res += b0;
			res += (((long) b1) << 8);
			res += (((long) b2) << 16);
			res += (((long) b3) << 24);
		} else {
			res += b3;
			res += (((long) b2) << 8);
			res += (((long) b1) << 16);
			res += (((long) b0) << 24);
		}
		return Float.intBitsToFloat(res);
	}

}
