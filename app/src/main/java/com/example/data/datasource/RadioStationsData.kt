package com.example.data.datasource

import com.example.data.model.RadioGenre
import com.example.data.model.RadioStation

object RadioStationsData {
    val sampleRadios: List<RadioStation> = listOf(
        // Notícias & Jornalismo
        RadioStation(
            id = "cbn_sp",
            name = "CBN São Paulo",
            dial = "90.5 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.NOTICIAS,
            streamUrl = "https://stream.sgr.globo.com/hls/aCBNSP/aCBNSP.m3u8",
            backupStreamUrl = "https://stream.sgr.globo.com/hls/aCBNRJ/aCBNRJ.m3u8",
            logoText = "CBN",
            accentColorHex = 0xFFD32F2F,
            currentShow = "Jornal da CBN • Notícias 24h",
            bitrate = "128 kbps HLS"
        ),
        RadioStation(
            id = "bandnews_sp",
            name = "BandNews FM",
            dial = "96.9 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.NOTICIAS,
            streamUrl = "https://evp.mm.uol.com.br:8088/bandnewsfm_sp/bandnewsfm_sp.stream/playlist.m3u8",
            backupStreamUrl = "https://evp.mm.uol.com.br:8088/radiobandeirantes_sp/radiobandeirantes_sp.stream/playlist.m3u8",
            logoText = "BN",
            accentColorHex = 0xFF1565C0,
            currentShow = "Em 20 Minutos Tudo Pode Mudar",
            bitrate = "128 kbps HLS"
        ),
        RadioStation(
            id = "jp_news",
            name = "Jovem Pan News",
            dial = "620 AM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.NOTICIAS,
            streamUrl = "http://live.webradio.upx.net.br:8112/",
            backupStreamUrl = "https://painel.radiojovempan.com.br:8000/stream",
            logoText = "JP NEWS",
            accentColorHex = 0xFFC62828,
            currentShow = "Jornal da Manhã & Opinião",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "radio_bandeirantes",
            name = "Rádio Bandeirantes",
            dial = "90.9 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.NOTICIAS,
            streamUrl = "https://evp.mm.uol.com.br:8088/radiobandeirantes_sp/radiobandeirantes_sp.stream/playlist.m3u8",
            backupStreamUrl = "https://evp.mm.uol.com.br:8088/bandnewsfm_sp/bandnewsfm_sp.stream/playlist.m3u8",
            logoText = "RB",
            accentColorHex = 0xFF0D47A1,
            currentShow = "O Pulo do Gato & Ciranda da Cidade",
            bitrate = "128 kbps HLS"
        ),
        RadioStation(
            id = "radio_senado",
            name = "Rádio Senado",
            dial = "107.9 FM",
            cityState = "Brasília - DF",
            genre = RadioGenre.NOTICIAS,
            streamUrl = "https://stream.senado.leg.br/senado",
            backupStreamUrl = "https://stream.camara.gov.br/radiocamara",
            logoText = "SENADO",
            accentColorHex = 0xFF00796B,
            currentShow = "Voz do Brasil e Sessões Plenárias",
            bitrate = "192 kbps MP3"
        ),
        RadioStation(
            id = "radio_nacional",
            name = "Rádio Nacional",
            dial = "980 AM",
            cityState = "Brasília - DF",
            genre = RadioGenre.NOTICIAS,
            streamUrl = "https://stream.ebc.com.br/nacional-am-df",
            backupStreamUrl = "https://stream.ebc.com.br/nacional-fm-df",
            logoText = "EBC",
            accentColorHex = 0xFF388E3C,
            currentShow = "Eu de Cá, Você de Lá • Brasilia",
            bitrate = "128 kbps MP3"
        ),

        // Flashback & Sofisticado
        RadioStation(
            id = "antena_um",
            name = "Antena 1",
            dial = "94.7 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.FLASHBACK,
            streamUrl = "https://antenaone.crossradio.com.br/stream/1",
            backupStreamUrl = "https://alpha.crossradio.com.br/stream/1",
            logoText = "A1",
            accentColorHex = 0xFF00A8E1,
            currentShow = "Música Clássica Pop & Soft Rock",
            bitrate = "192 kbps MP3"
        ),
        RadioStation(
            id = "alpha_fm",
            name = "Alpha FM",
            dial = "101.7 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.FLASHBACK,
            streamUrl = "https://alpha.crossradio.com.br/stream/1",
            backupStreamUrl = "https://antenaone.crossradio.com.br/stream/1",
            logoText = "ALPHA",
            accentColorHex = 0xFF8E24AA,
            currentShow = "Grandes Sucessos dos Anos 80, 90 e 2000",
            bitrate = "192 kbps MP3"
        ),
        RadioStation(
            id = "jb_fm",
            name = "JB FM Rio",
            dial = "99.9 FM",
            cityState = "Rio de Janeiro - RJ",
            genre = RadioGenre.FLASHBACK,
            streamUrl = "https://antenaone.crossradio.com.br/stream/1",
            backupStreamUrl = "https://alpha.crossradio.com.br/stream/1",
            logoText = "JB",
            accentColorHex = 0xFF0288D1,
            currentShow = "Sucessos Inesquecíveis e Informação",
            bitrate = "192 kbps MP3"
        ),
        RadioStation(
            id = "saudade_fm",
            name = "Saudade FM",
            dial = "99.7 FM",
            cityState = "Santos - SP",
            genre = RadioGenre.FLASHBACK,
            streamUrl = "https://saudade.crossradio.com.br/stream",
            backupStreamUrl = "https://antenaone.crossradio.com.br/stream/1",
            logoText = "SAUDADE",
            accentColorHex = 0xFFF57C00,
            currentShow = "Túnel do Tempo 70, 80 & 90",
            bitrate = "128 kbps MP3"
        ),

        // MPB & Bossa Nova
        RadioStation(
            id = "novabrasil_fm",
            name = "Novabrasil FM",
            dial = "89.7 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.MPB,
            streamUrl = "https://novabrasil.crossradio.com.br/stream",
            backupStreamUrl = "https://stream.zeno.fm/h2w8e4rndg8uv",
            logoText = "NOVA",
            accentColorHex = 0xFF00897B,
            currentShow = "O Melhor da Música Brasileira MPB",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "mec_fm",
            name = "MEC FM",
            dial = "99.3 FM",
            cityState = "Rio de Janeiro - RJ",
            genre = RadioGenre.MPB,
            streamUrl = "https://stream.ebc.com.br/mec-fm-rj",
            backupStreamUrl = "https://stream.ebc.com.br/mec-am-rj",
            logoText = "MEC",
            accentColorHex = 0xFF5D4037,
            currentShow = "Concerto MEC & Clássicos Nacionais",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "eldorado_fm",
            name = "Rádio Eldorado",
            dial = "107.3 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.MPB,
            streamUrl = "https://eldorado.crossradio.com.br/stream",
            backupStreamUrl = "https://stream.zeno.fm/eldorado",
            logoText = "ELDORADO",
            accentColorHex = 0xFFFF8F00,
            currentShow = "Vozes do Brasil & MPB Contemporânea",
            bitrate = "128 kbps MP3"
        ),

        // Pop & Hits Jovem
        RadioStation(
            id = "jp_fm",
            name = "Jovem Pan FM",
            dial = "100.9 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.POP,
            streamUrl = "http://live.webradio.upx.net.br:8112/",
            backupStreamUrl = "https://painel.radiojovempan.com.br:8000/stream",
            logoText = "JP FM",
            accentColorHex = 0xFFE53935,
            currentShow = "Pânico no Rádio & Top Hits",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "mix_fm",
            name = "Mix FM",
            dial = "106.3 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.POP,
            streamUrl = "https://mix.crossradio.com.br/stream",
            backupStreamUrl = "https://antenaone.crossradio.com.br/stream/1",
            logoText = "MIX",
            accentColorHex = 0xFF3949AB,
            currentShow = "Top Mix e Lançamentos Internacionais",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "bh_fm",
            name = "BH FM",
            dial = "102.1 FM",
            cityState = "Belo Horizonte - MG",
            genre = RadioGenre.POP,
            streamUrl = "https://stream.sgr.globo.com/hls/aBHFM/aBHFM.m3u8",
            backupStreamUrl = "https://stream.sgr.globo.com/hls/aCBNBH/aCBNBH.m3u8",
            logoText = "BH FM",
            accentColorHex = 0xFF00ACC1,
            currentShow = "A Rádio Que Toca Tudo • Minas",
            bitrate = "128 kbps HLS"
        ),

        // Rock
        RadioStation(
            id = "89_rock",
            name = "89 A Rádio Rock",
            dial = "89.1 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.ROCK,
            streamUrl = "https://89.crossradio.com.br/stream",
            backupStreamUrl = "https://stream.zeno.fm/w062e78ndg8uv",
            logoText = "89 ROCK",
            accentColorHex = 0xFF212121,
            currentShow = "Do Balacobaco & Rock Nacional e Gringo",
            bitrate = "192 kbps MP3"
        ),
        RadioStation(
            id = "kiss_fm",
            name = "Kiss FM",
            dial = "92.5 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.ROCK,
            streamUrl = "https://kissfm.crossradio.com.br/stream",
            backupStreamUrl = "https://ice.fabricahost.com.br/kissfmsp",
            logoText = "KISS FM",
            accentColorHex = 0xFFB71C1C,
            currentShow = "Classic Rock 24h Não Para Nunca",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "cidade_rock",
            name = "Rádio Cidade Rock",
            dial = "Web HD",
            cityState = "Rio de Janeiro - RJ",
            genre = RadioGenre.ROCK,
            streamUrl = "https://stream.zeno.fm/ciderock",
            backupStreamUrl = "https://89.crossradio.com.br/stream",
            logoText = "CIDADE",
            accentColorHex = 0xFF424242,
            currentShow = "Invasão da Cidade • Rock Puro",
            bitrate = "128 kbps MP3"
        ),

        // Sertanejo
        RadioStation(
            id = "nativa_fm",
            name = "Nativa FM",
            dial = "95.3 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.SERTANEJO,
            streamUrl = "https://evp.mm.uol.com.br:8088/nativafm_sp/nativafm_sp.stream/playlist.m3u8",
            backupStreamUrl = "https://stream.zeno.fm/nativafm",
            logoText = "NATIVA",
            accentColorHex = 0xFF2E7D32,
            currentShow = "Hora do Chumbo & Sertanejo do Bom",
            bitrate = "128 kbps HLS"
        ),
        RadioStation(
            id = "positiva_fm",
            name = "Positiva FM",
            dial = "99.1 FM",
            cityState = "Goiânia - GO",
            genre = RadioGenre.SERTANEJO,
            streamUrl = "https://stream.zeno.fm/positivafm",
            backupStreamUrl = "https://ice.fabricahost.com.br/positivago",
            logoText = "POSITIVA",
            accentColorHex = 0xFFFF6F00,
            currentShow = "O Coração do Sertanejo Universitário",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "gazeta_fm",
            name = "Gazeta FM",
            dial = "88.1 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.SERTANEJO,
            streamUrl = "https://stream.zeno.fm/gazetafm",
            backupStreamUrl = "https://ice.fabricahost.com.br/gazetafm",
            logoText = "GAZETA",
            accentColorHex = 0xFFC2185B,
            currentShow = "Primeira Linha e Hits do Brasil",
            bitrate = "128 kbps MP3"
        ),

        // Samba & Pagode
        RadioStation(
            id = "radio_mania",
            name = "Rádio Mania",
            dial = "102.9 FM",
            cityState = "Rio de Janeiro - RJ",
            genre = RadioGenre.SAMBA_PAGODE,
            streamUrl = "https://stream.zeno.fm/radiomania",
            backupStreamUrl = "https://ice.fabricahost.com.br/radiomania",
            logoText = "MANIA",
            accentColorHex = 0xFFFF3D00,
            currentShow = "Ao Vivo Acústico & Pagode de Mesa",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "transcontinental_fm",
            name = "Transcontinental FM",
            dial = "104.7 FM",
            cityState = "São Paulo - SP",
            genre = RadioGenre.SAMBA_PAGODE,
            streamUrl = "https://stream.zeno.fm/transcontinental",
            backupStreamUrl = "https://ice.fabricahost.com.br/transcontinental",
            logoText = "TRANSCO",
            accentColorHex = 0xFF512DA8,
            currentShow = "Acústico Trans & O Melhor do Pagode",
            bitrate = "128 kbps MP3"
        ),

        // Gospel & Religiosa
        RadioStation(
            id = "radio_melodia",
            name = "Rádio Melodia",
            dial = "97.5 FM",
            cityState = "Rio de Janeiro - RJ",
            genre = RadioGenre.GOSPEL,
            streamUrl = "https://stream.zeno.fm/uqqvbbqmdg8uv",
            backupStreamUrl = "https://ice.fabricahost.com.br/melodiafm",
            logoText = "MELODIA",
            accentColorHex = 0xFF1976D2,
            currentShow = "Louvor e Adoração com a Melodia",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "radio_aparecida",
            name = "Rádio Aparecida",
            dial = "104.3 FM",
            cityState = "Aparecida - SP",
            genre = RadioGenre.GOSPEL,
            streamUrl = "https://painel.radioaparecida.com.br:8000/stream",
            backupStreamUrl = "https://stream.zeno.fm/aparecida",
            logoText = "APARECIDA",
            accentColorHex = 0xFF0D47A1,
            currentShow = "Missa no Santuário e Santo Terço",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "sara_brasil",
            name = "Sara Brasil FM",
            dial = "101.3 FM",
            cityState = "Curitiba - PR",
            genre = RadioGenre.GOSPEL,
            streamUrl = "https://stream.zeno.fm/sarabrasil",
            backupStreamUrl = "https://stream.zeno.fm/uqqvbbqmdg8uv",
            logoText = "SARA",
            accentColorHex = 0xFF7B1FA2,
            currentShow = "Palavra de Vida & Louvores",
            bitrate = "128 kbps MP3"
        ),

        // Esportes & Futebol
        RadioStation(
            id = "radio_gaucha",
            name = "Rádio Gaúcha",
            dial = "93.7 FM",
            cityState = "Porto Alegre - RS",
            genre = RadioGenre.ESPORTES,
            streamUrl = "https://stream.zeno.fm/gauchafm",
            backupStreamUrl = "https://rbs-gaucha-poa.stream.gruporbs.com.br/stream",
            logoText = "GAÚCHA",
            accentColorHex = 0xFF388E3C,
            currentShow = "Sala de Redação & Pré-Jornada",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "radio_itatiaia",
            name = "Rádio Itatiaia",
            dial = "95.7 FM",
            cityState = "Belo Horizonte - MG",
            genre = RadioGenre.ESPORTES,
            streamUrl = "https://stream.zeno.fm/5yrmb1rndg8uv",
            backupStreamUrl = "https://ice.fabricahost.com.br/itatiaiaam",
            logoText = "ITATIAIA",
            accentColorHex = 0xFFB71C1C,
            currentShow = "A Mais Esportiva do Brasil • Minas",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "radio_guaiba",
            name = "Rádio Guaíba",
            dial = "101.3 FM",
            cityState = "Porto Alegre - RS",
            genre = RadioGenre.ESPORTES,
            streamUrl = "https://stream.zeno.fm/guaiba",
            backupStreamUrl = "https://stream.zeno.fm/gauchafm",
            logoText = "GUAÍBA",
            accentColorHex = 0xFF00796B,
            currentShow = "Guaíba Esporte & Jornalismo RS",
            bitrate = "128 kbps MP3"
        ),

        // Forró & Piseiro / Regional
        RadioStation(
            id = "somzoom_sat",
            name = "Somzoom Sat",
            dial = "Rede Sat",
            cityState = "Fortaleza - CE",
            genre = RadioGenre.FORRO,
            streamUrl = "https://stream.zeno.fm/somzoom",
            backupStreamUrl = "https://stream.zeno.fm/forro",
            logoText = "SOMZOOM",
            accentColorHex = 0xFFFF8F00,
            currentShow = "Forrozão das Antigas e Piseiro",
            bitrate = "128 kbps MP3"
        ),
        RadioStation(
            id = "jangadeiro_fm",
            name = "Jangadeiro FM",
            dial = "88.9 FM",
            cityState = "Fortaleza - CE",
            genre = RadioGenre.FORRO,
            streamUrl = "https://stream.zeno.fm/jangadeiro",
            backupStreamUrl = "https://stream.zeno.fm/somzoom",
            logoText = "JANGA",
            accentColorHex = 0xFF0288D1,
            currentShow = "Forró & Notícias do Nordeste",
            bitrate = "128 kbps MP3"
        )
    )
}
