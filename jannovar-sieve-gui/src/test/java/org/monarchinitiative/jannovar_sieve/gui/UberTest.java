package org.monarchinitiative.jannovar_sieve.gui;

import com.google.common.collect.ImmutableCollection;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * This test tries to deserialize one small Jannovar database that has been created by this Gui. Content of the database
 * is checked.
 */
public class UberTest {

    @Test
    public void uberTest() throws Exception {
        final File dbPath = new File(UberTest.class.getResource("smallDb.ser").getFile());
        assertTrue(dbPath.isFile());

        final JannovarDataSerializer serializer = new JannovarDataSerializer(dbPath.getAbsolutePath());
        final JannovarData jannovarData = serializer.load();

        // should contain 4 genes
        assertThat(jannovarData.getTmByGeneSymbol().keySet().size(), is(4));
        assertThat(jannovarData.getTmByGeneSymbol().keySet(), hasItems("GCK", "HNF4A", "TNC", "SURF1"));

        // should contain 2 transcripts
        final ImmutableCollection<TranscriptModel> surf1Transcripts = jannovarData.getTmByGeneSymbol().get("SURF1");
        assertThat(surf1Transcripts.size(), is(2));
    }
}