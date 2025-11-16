package com.foreverrafs.superdiary.ai

import br.com.vexpera.ktoon.Toon

class AndroidToonEncoder : ToonEncoder {
    override fun encode(json: String): String = Toon.encode(json)
}
