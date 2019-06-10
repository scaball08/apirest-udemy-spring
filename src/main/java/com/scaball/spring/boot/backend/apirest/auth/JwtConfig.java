package com.scaball.spring.boot.backend.apirest.auth;

/*Clase que contiene nuestra clave Secreta  tanto la publica como 
 * la privada para  la encriptacion Open ssl RSA
 * */
public class JwtConfig {
	
	public static final String LLAVE_SECRETA = "alguna.clave.secreta.12345678";
	
	public static final String RSA_PRIVADA = "-----BEGIN RSA PRIVATE KEY-----\r\n" + 
			"MIIBPAIBAAJBALaq66oUUz8V1efGoLU5K2UTE904UrecZMa3uxWugnNGiliq9wRG\r\n" + 
			"IFO1yUZUokZjlbSgJsWkUzBA4b7AiohdMV8CAwEAAQJBAICtwRvr+4SqRihjRRzE\r\n" + 
			"rCwkl060eZi8xEU0csdgSqcW7NQYR/l0bV5ErfINTpJEGu2ysLQ7y/7vA+h5EmqM\r\n" + 
			"zkECIQDwAswTr2dPRz5DkLKvCHzz3MjLQfbEMPi6/hvGwh8bfwIhAMLWLYRXEc39\r\n" + 
			"T9iVEgpmg9FTqNqp4umBbjVLaNWFUFohAiBC4SSo35m0F7Ab97gy2fSnp4A0U19F\r\n" + 
			"8Dd/M8vdqL4uVQIhAJRokEM+D1HYoflXk2ZYsA/Cqdvli/JRpKDzz+bUpWABAiEA\r\n" + 
			"6/csEZK0iDL9n8hI1h1MDr1rdZrkumEwek6zRvWosN8=\r\n" + 
			"-----END RSA PRIVATE KEY-----" ;
	
	public static final String RSA_PUBLICA = "-----BEGIN PUBLIC KEY-----\r\n" + 
			"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALaq66oUUz8V1efGoLU5K2UTE904Urec\r\n" + 
			"ZMa3uxWugnNGiliq9wRGIFO1yUZUokZjlbSgJsWkUzBA4b7AiohdMV8CAwEAAQ==\r\n" + 
			"-----END PUBLIC KEY-----" ;

}
