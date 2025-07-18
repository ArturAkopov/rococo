
�
artist.protoanbrain.qa.rococo.grpc"
ArtistRequest
id (	Rid";
AllArtistsRequest
page (Rpage
size (Rsize"R
SearchArtistsRequest
name (	Rname
page (Rpage
size (Rsize"]
CreateArtistRequest
name (	Rname
	biography (	R	biography
photo (	Rphoto"m
UpdateArtistRequest
id (	Rid
name (	Rname
	biography (	R	biography
photo (	Rphoto"h
ArtistResponse
id (	Rid
name (	Rname
	biography (	R	biography
photo (	Rphoto"w
AllArtistsResponse@
artists (2&.anbrain.qa.rococo.grpc.ArtistResponseRartists
total_count (R
totalCount2�
ArtistServiceZ
	GetArtist%.anbrain.qa.rococo.grpc.ArtistRequest&.anbrain.qa.rococo.grpc.ArtistResponsef
GetAllArtists).anbrain.qa.rococo.grpc.AllArtistsRequest*.anbrain.qa.rococo.grpc.AllArtistsResponseo
SearchArtistsByName,.anbrain.qa.rococo.grpc.SearchArtistsRequest*.anbrain.qa.rococo.grpc.AllArtistsResponsec
CreateArtist+.anbrain.qa.rococo.grpc.CreateArtistRequest&.anbrain.qa.rococo.grpc.ArtistResponsec
UpdateArtist+.anbrain.qa.rococo.grpc.UpdateArtistRequest&.anbrain.qa.rococo.grpc.ArtistResponseB'
anbrain.qa.rococo.grpcBArtistProtoPJ�
  F

  

 "
	

 "

 /
	
 /

 ,
	
 ,

 


  


 
:
  
8- Получить художника по ID


  


  


  
(6
U
 DH Получить всех художников с пагинацией


 

 %

 0B
>
 M1 Поиск художников по имени


 

 .

 9K
=
 A0 Создать нового художника


 

 &

 1?
?
 A2 Обновить данные художника


 

 &

 1?
5
  ) Запрос художника по ID



 

  

  

  	

  
P
 "D Запрос всех художников с пагинацией





  

  

  

  

!

!

!

!
L
% )@ Запрос поиска художников по имени



%

 &

 &

 &	

 &

'

'

'

'

(

(

(

(
>
, 02 Запрос создания художника



,

 -

 -

 -	

 -

.

.

.	

.

/

/

/	

/
B
3 86 Запрос обновления художника



3

 4

 4

 4	

 4

5

5

5	

5

6

6

6	

6

7

7

7	

7
=
; @1 Ответ с данными художника



;

 <

 <

 <	

 <

=

=

=	

=

>

>

>	

>

?

?

?	

?
A
C F5 Ответ со списком художников



C

 D&

 D


 D

 D!

 D$%

E

E

E

Ebproto3
�
common.protoanbrain.qa.rococo.grpc"T
Geo
city (	Rcity9
country (2.anbrain.qa.rococo.grpc.CountryRcountry"-
Country
id (	Rid
name (	RnameB'
anbrain.qa.rococo.grpcBCommonProtoPJ�
  

  

 

 "
	

 "

 /
	
 /

 ,
	
 ,


  


 

  	

  	

  		

  	

 


 
	

 



 



 




 

 

 	

 





	

bproto3
�!
museum.protoanbrain.qa.rococo.grpccommon.proto"
MuseumRequest
id (	Rid";
AllMuseumsRequest
page (Rpage
size (Rsize",
SearchMuseumsRequest
title (	Rtitle"�
CreateMuseumRequest
title (	Rtitle 
description (	Rdescription
photo (	Rphoto-
geo (2.anbrain.qa.rococo.grpc.GeoRgeo"�
UpdateMuseumRequest
id (	Rid
title (	Rtitle 
description (	Rdescription
photo (	Rphoto-
geo (2.anbrain.qa.rococo.grpc.GeoRgeo"_
AllCountriesRequest
page (Rpage
size (Rsize
name (	H Rname�B
_name"�
MuseumResponse
id (	Rid
title (	Rtitle 
description (	Rdescription
photo (	Rphoto-
geo (2.anbrain.qa.rococo.grpc.GeoRgeo"w
AllMuseumsResponse@
museums (2&.anbrain.qa.rococo.grpc.MuseumResponseRmuseums
total_count (R
totalCount"Y
SearchMuseumsResponse@
museums (2&.anbrain.qa.rococo.grpc.MuseumResponseRmuseums"v
AllCountriesResponse=
	countries (2.anbrain.qa.rococo.grpc.CountryR	countries
total_count (R
totalCount2�
MuseumServiceZ
	GetMuseum%.anbrain.qa.rococo.grpc.MuseumRequest&.anbrain.qa.rococo.grpc.MuseumResponsef
GetAllMuseums).anbrain.qa.rococo.grpc.AllMuseumsRequest*.anbrain.qa.rococo.grpc.AllMuseumsResponses
SearchMuseumsByTitle,.anbrain.qa.rococo.grpc.SearchMuseumsRequest-.anbrain.qa.rococo.grpc.SearchMuseumsResponsec
CreateMuseum+.anbrain.qa.rococo.grpc.CreateMuseumRequest&.anbrain.qa.rococo.grpc.MuseumResponsec
UpdateMuseum+.anbrain.qa.rococo.grpc.UpdateMuseumRequest&.anbrain.qa.rococo.grpc.MuseumResponse2~
CountryServicel
GetAllCountries+.anbrain.qa.rococo.grpc.AllCountriesRequest,.anbrain.qa.rococo.grpc.AllCountriesResponseB'
anbrain.qa.rococo.grpcBMuseumProtoPJ�
  R

  

 

 "
	

 "

 /
	
 /

 ,
	
 ,
	
  


 
 


 

2
  8% Получить музей по ID


  

  

  (6
1
 D$ Получить все музеи


 

 %

 0B
@
 Q3 Получить музей по названию


 

 /

 :O
*
 A Добавить музей


 

 &

 1?
*
 A Обновить музей


 

 &

 1?


 



3
 J& Получить все страны


 

 )

 4H


  


 

  

  

  	

  


  #


 

 !

 !

 !

 !

"

"

"

"


% '


%

 &

 &

 &	

 &


) .


)

 *

 *

 *	

 *

+

+

+	

+

,

,

,	

,

-

-

-	

-


0 6


0

 1

 1

 1	

 1

2

2

2	

2

3

3

3	

3

4

4

4	

4

5

5

5	

5


8 <


8

 9

 9

 9

 9

:

:

:

:

;

;


;

;

;


> D


>

 ?

 ?

 ?	

 ?

@

@

@	

@

A

A

A	

A

B

B

B	

B

C

C

C	

C


F I


F

 G&

 G


 G

 G!

 G$%

H

H

H

H


K M


K

 L&

 L


 L

 L!

 L$%


	O R


	O

	 P!

	 P


	 P

	 P

	 P 

	Q

	Q

	Q

	Qbproto3
�%
painting.protoanbrain.qa.rococo.grpc"!
PaintingRequest
id (	Rid"=
AllPaintingsRequest
page (Rpage
size (Rsize"_
PaintingsByArtistRequest
	artist_id (	RartistId
page (Rpage
size (Rsize"W
PaintingsByTitleRequest
title (	Rtitle
page (Rpage
size (Rsize"�
CreatePaintingRequest
title (	Rtitle 
description (	Rdescription
content (	Rcontent
	museum_id (	RmuseumId
	artist_id (	RartistId"�
UpdatePaintingRequest
id (	Rid
title (	Rtitle 
description (	Rdescription
content (	Rcontent
	museum_id (	RmuseumId
	artist_id (	RartistId"�
PaintingResponse
id (	Rid
title (	Rtitle 
description (	Rdescription
content (	Rcontent
	museum_id (	RmuseumId
	artist_id (	RartistId"
AllPaintingsResponseF
	paintings (2(.anbrain.qa.rococo.grpc.PaintingResponseR	paintings
total_count (R
totalCount2�
PaintingService`
GetPainting'.anbrain.qa.rococo.grpc.PaintingRequest(.anbrain.qa.rococo.grpc.PaintingResponsel
GetAllPaintings+.anbrain.qa.rococo.grpc.AllPaintingsRequest,.anbrain.qa.rococo.grpc.AllPaintingsResponsev
GetPaintingsByArtist0.anbrain.qa.rococo.grpc.PaintingsByArtistRequest,.anbrain.qa.rococo.grpc.AllPaintingsResponset
GetPaintingsByTitle/.anbrain.qa.rococo.grpc.PaintingsByTitleRequest,.anbrain.qa.rococo.grpc.AllPaintingsResponsei
CreatePainting-.anbrain.qa.rococo.grpc.CreatePaintingRequest(.anbrain.qa.rococo.grpc.PaintingResponsei
UpdatePainting-.anbrain.qa.rococo.grpc.UpdatePaintingRequest(.anbrain.qa.rococo.grpc.PaintingResponseB)
anbrain.qa.rococo.grpcBPaintingProtoPJ�
  J

  

 

 "
	

 "

 /
	
 /

 .
	
 .


  


 
6
  
>) Получить картину по ID


  


  
!

  
,<
Z
 JM Получить список всех картин с пагинацией


 

 )

 4H
I
 T< Получить картины по ID художника


 

 3

 >R
D
 S7 Получить картины по названию


 

 2

 =Q
9
 G, Добавить новую картину


 

 *

 5E
T
 GG Обновить данные существующей картины


 

 *

 5E
I
  = Запрос на получение картины по ID



 

  

  

  	

  
d
 X Запрос на получение списка картин с пагинацией





 

 

 

 








o
! %c Запрос на получение картин по ID художника + пагинация



! 

 "

 "

 "	

 "

#

#

#

#

$

$

$

$
U
' +I Запрос на получение картин по названию



'

 (

 (

 (	

 (

)

)

)

)

*

*

*

*
?
- 33 Запрос на создание картины



-

 .

 .

 .	

 .

/

/

/	

/

0

0

0	

0

1

1

1	

1

2

2

2	

2
C
5 <7 Запрос на обновление картины



5

 6

 6

 6	

 6

7

7

7	

7

8

8

8	

8

9

9

9	

9

:

:

:	

:

;

;

;	

;
<
> E0 Ответ с данными о картине



>

 ?

 ?

 ?	

 ?

@

@

@	

@

A

A

A	

A

B

B

B	

B

C

C

C	

C

D

D

D	

D
T
G JH Ответ с пагинированным списком картин



G

 H*

 H


 H

 H%

 H()

I

I

I

Ibproto3
�
userdata.proto")
UserRequest
username (	Rusername"�
UserResponse
id (	Rid
username (	Rusername
	firstname (	R	firstname
lastname (	Rlastname
avatar (	Ravatar"�
UpdateUserRequest
username (	Rusername
	firstname (	R	firstname
lastname (	Rlastname
avatar (	Ravatar2g
Userdata(
GetUser.UserRequest.UserResponse" 1

UpdateUser.UpdateUserRequest.UserResponse" B)
anbrain.qa.rococo.grpcBUserdataProtoPJ�
  !

  

 "
	

 "

 /
	
 /

 .
	
 .


  


 
8
  5+ Получить пользователя


  

  

  %1
8
 
>+ Обновить пользователя


 


 
#

 
.:
K
  ? Запрос на получение пользователя



 

  

  

  	

  
C
 7 Ответ с данными пользователя





 

 

 	

 





	







	







	







	


\
 !P Ответ с обновленными данными пользователя





 

 

 	

 





	







	



 

 

 	

 bproto3