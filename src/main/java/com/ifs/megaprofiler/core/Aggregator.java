/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ifs.megaprofiler.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.digester3.*;
import org.xml.sax.SAXException;

import com.ifs.megaprofiler.elements.Document;

/**
 * 
 * @author artur
 */
public class Aggregator {
	List<String> Vocabulary;
	Digester digester;

	public Aggregator() {
		this.digester = new Digester(); // not thread safe
		this.digester.setRules(new RegexRules(new SimpleRegexMatcher()));
		this.createParsingRules();
		Vocabulary = new ArrayList<String>();
	}

	public Document ParseDocument(InputStream input) {
		DigesterContext context = new DigesterContext();
		this.digester.push(context);

		try {
			context = (DigesterContext) this.digester.parse(new InputStreamReader(
					input));
                        input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return context.getDocument();
	}

	private void createParsingRules() {
		this.createDocumentRules();
		this.createIdentificationRules();
		// TODO this.createFileInfoRules();
	}

	private void createDocumentRules() {
		this.digester.addCallMethod("fits", "createDocument", 2);
		this.digester.addCallParam("fits/fileinfo/filename", 0);
		this.digester.addCallParam("fits/fileinfo/filepath", 1);
	}

	private void createIdentificationRules() {
		this.createIdentificationStatusRule("fits/identification");
		this.createIdentityRule("fits/identification/identity");
		this.createIdentityToolRule("fits/identification/identity/tool");
		this.createIdentityVersionRule("fits/identification/identity/version");
		this.createPuidRule("fits/identification/identity/externalIdentifier");

	}

	private void createIdentificationStatusRule(String pattern) {
		this.digester.addCallMethod(pattern, "createIdentification", 1);
		this.digester.addCallParam(pattern, 0, "status");
	}

	private void createIdentityRule(String pattern) {
		this.digester.addCallMethod(pattern, "createIdentity", 2);
		this.digester.addCallParam(pattern, 0, "format");
		this.digester.addCallParam(pattern, 1, "mimetype");
	}

	private void createIdentityToolRule(String pattern) {
		this.digester.addCallMethod(pattern, "setIdentityTool", 2);
		this.digester.addCallParam(pattern, 0, "toolname");
		this.digester.addCallParam(pattern, 1, "toolversion");
	}

	private void createIdentityVersionRule(String pattern) {
		this.digester.addCallMethod(pattern, "setIdentityVersion", 4);
		this.digester.addCallParam(pattern, 0);
		this.digester.addCallParam(pattern, 1, "status");
		this.digester.addCallParam(pattern, 2, "toolname");
		this.digester.addCallParam(pattern, 3, "toolversion");
	}

	private void createPuidRule(String pattern) {
		this.digester.addCallMethod(pattern, "setIdentityPuid", 3);
		this.digester.addCallParam(pattern, 0);
		this.digester.addCallParam(pattern, 1, "toolname");
		this.digester.addCallParam(pattern, 2, "toolversion");
	}

}
