package com.googlecode.graph2svg.tools.jrav;

import java.io.File;
import java.io.IOException;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.googlecode.graph2svg.tools.fileFilter.RegExpFileFilter;
import com.googlecode.graph2svg.tools.jrav.BatchValidator;

/**
 * Validating XML against Relax NG schema.
 * 
 * @deprecated use {@link BatchValidator} instead.
 */
public class BatchRelaxNgValidator {

	public static void main(String[] args)
			throws VerifierConfigurationException, SAXException, IOException {
		// create a VerifierFactory
		VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();

		if (args.length < 2) {
			printUsage();
			return;
		}

		String rnSchemaFile = args[0]; // "xygr.rng";
		// compile a RELAX schema (or whatever schema you like)
		Schema schema = factory.compileSchema(new File(rnSchemaFile));

		// obtain a verifier
		Verifier verifier = schema.newVerifier();
		MyErrorHandler myErrorHandler = new MyErrorHandler("RelaxNG");
		verifier.setErrorHandler(myErrorHandler);

		String fileName = args[1]; // "gr7.xml";

		File file = new File(fileName);

		if (file.isDirectory()) {
			String filterString;
			if (args.length >= 3) {
				filterString = args[2];
			} else {
				filterString = ".*\\.xml"; // default
			}
			File[] fileList = file
					.listFiles(new RegExpFileFilter(filterString));
			for (File f : fileList) {
				myErrorHandler.reset();
				verifyFile(verifier, f);
				myErrorHandler.printMessages();
			}
		} else {
			verifyFile(verifier, file);
			myErrorHandler.printMessages();
		}
	}

	private static void verifyFile(Verifier verifier, File xmlFile)
			throws SAXException, IOException {
		System.out.print(xmlFile + ": ");
		try {
			if (verifier.verify(xmlFile)) {
				System.out.println(" ... ok");
			} else {
				System.out.println(" ... NOT VALID");
			}
		} catch (SAXParseException e) {
			System.out.println(" ... exception during validation - NOT VALID");
		}
	}

	private static void printUsage() {
		System.out.println("Two or tree arguments are expected:");
		System.out
				.println("\n   schemFile - a RelaxNG validation schema"
						+ "\n   xmlFileDir - directory where xml files are"
						+ "\n   filter - regEx filter string - optional, defaul: '.*\\.xml'");

	}

}
